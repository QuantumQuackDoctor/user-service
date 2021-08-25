package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.database.ormlibrary.user.*;
import com.ss.user.errors.ConfirmationTokenExpiredException;
import com.ss.user.errors.InvalidAdminEmailException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final AmazonSimpleEmailService emailService;
    @Value("${user-portal-url}")
    private String userPortalURL;
    @Value("${admin-portal-url}")
    private String adminPortalURL;
    @Value("${email.sender}")
    private String emailFrom;

    public UserService(UserRepo userRepo, UserRoleRepo userRoleRepo, ModelMapper mapper, PasswordEncoder passwordEncoder, AmazonSimpleEmailService emailService) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public boolean emailAvailable(String email) {
        return !userRepo.existsByEmail(email);
    }

    private static final String admin = "admin";

    public void insertUser(User user, boolean isAdmin) throws InvalidAdminEmailException {
        if (!emailAvailable(user.getEmail())) return;
        UserEntity toInsert = convertToEntity(user);
        //set defaults
        toInsert.setId(null);
        toInsert.setPoints(0);
        toInsert.setActivated(false);
        toInsert.setPassword(passwordEncoder.encode(user.getPassword()));

        if (!isAdmin) {
            Optional<UserRoleEntity> role = userRoleRepo.findByRole("user");
            if (!role.isPresent()) {
                role = Optional.of(userRoleRepo.save(new UserRoleEntity().setRole("user")));
            }
            toInsert.setUserRole(role.get());
        } else {
            if (user.getEmail().endsWith("@smoothstack.com")) {
                Optional<UserRoleEntity> role = userRoleRepo.findByRole(admin);
                if (!role.isPresent()) {
                    role = Optional.of(userRoleRepo.save(new UserRoleEntity().setRole(admin)));
                }
                toInsert.setUserRole(role.get());
            } else {
                throw new InvalidAdminEmailException("invalid email for admin account");
            }
        }

        //create activation token
        toInsert.setActivationToken(UUID.randomUUID());
        toInsert.setActivationTokenExpiration(Instant.now().plusMillis(7200000));

        sendActivationEmail(toInsert.getEmail(), toInsert.getActivationToken(), isAdmin ? adminPortalURL : userPortalURL);

        userRepo.save(toInsert);
    }

    private void sendActivationEmail(String recipient, UUID uuid, String portalUrl) {

        String activationLink = portalUrl + "/activate/" + uuid.toString();

//        request.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//        request.setFrom("ezra.john.mitchell@gmail.com");
//        request.setSubject("Scrumptious account activation");

        String htmlBody = String.format(
                "<a href=\"%s\"><h1 style=\"background-color: #2aa4d2; color: #f79e0; padding: 1em; text-decoration: none;\">Activate your account</h1></a>", activationLink) +
                "\nFollow this link to activate your account ";
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(recipient))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBody)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData("Scrumptious Account Activation")))
                .withSource(emailFrom);

        emailService.sendEmail(request);
    }

    public void activateAccount(UUID uuid) throws ConfirmationTokenExpiredException, UserNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepo.findByActivationToken(uuid);
        if (userEntityOptional.isPresent()) {
            UserEntity userToActivate = userEntityOptional.get();
            if (Instant.now().isBefore(userToActivate.getActivationTokenExpiration())) {
                userToActivate.setActivated(true);
                userToActivate.setActivationToken(null);
                userToActivate.setActivationTokenExpiration(null);
                userRepo.save(userToActivate);
            } else {
                userToActivate.setActivated(false);
                userToActivate.setActivationToken(UUID.randomUUID());
                userToActivate.setActivationTokenExpiration(Instant.now().plusMillis(7200000));

                sendActivationEmail(userToActivate.getEmail(), userToActivate.getActivationToken(), userToActivate.getUserRole().getRole().equals(admin) ? adminPortalURL : userPortalURL);
                userRepo.save(userToActivate);
                throw new ConfirmationTokenExpiredException("Confirmation token expired");
            }
        } else
            throw new UserNotFoundException("Token invalid");
    }

    public User getUser(String email) throws UserNotFoundException {
        Optional<UserEntity> entity = userRepo.findByEmail(email);
        if (entity.isPresent()) {
            return convertToDTO(entity.get());
        } else throw new UserNotFoundException("User not found");
    }

    public void deleteUser(Long id) {
        //TODO delete orders
        userRepo.deleteById(id);
    }


    private UserEntity convertToEntity(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);
        //populate settings, modelMapper cannot get these
        DriverService.convertSettingsToEntity(entity, user.getSettings(), formatter, user.getDOB());
        return entity;
    }

    private User convertToDTO(UserEntity entity) {;
        User user = mapper.map(entity, User.class);
        user.setDOB(entity.getBirthDate().format((formatter)));
        user.getSettings().getNotifications().setEmail(entity.getSettings().getNotifications().getEmail());
        user.getSettings().getNotifications().setText(entity.getSettings().getNotifications().getPhoneOption());
        user.getSettings().setTheme(entity.getSettings().getThemes().getDark() ? UserSettings.ThemeEnum.DARK : UserSettings.ThemeEnum.LIGHT);

        user.setPassword(null);
        return user;
    }
}
