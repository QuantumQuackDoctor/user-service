package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.ss.user.errors.ConfirmationTokenExpiredException;
import com.ss.user.errors.InvalidAdminEmailException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.OrderRepo;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j (topic = "UserServiceLogs")
public class UserService {

    private static final String admin = "admin";
    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final OrderRepo orderRepo;
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

    public UserService(UserRepo userRepo, UserRoleRepo userRoleRepo, OrderRepo orderRepo, ModelMapper mapper, PasswordEncoder passwordEncoder, AmazonSimpleEmailService emailService) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.orderRepo = orderRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public boolean emailAvailable(String email) {
        return !userRepo.existsByEmail(email);
    }

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
                log.info("Invalid Admin Account creation attempted with - " + user.getEmail());
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

    public UserSettings updateNotifications(Long userId, UserSettings userSettings) throws UserNotFoundException {
        log.info(userSettings.toString());
        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            UserEntity entity = userEntityOptional.get();

            if (!entity.getSettings().getNotifications().getEmail() && userSettings.getNotifications().getEmail()) {
                entity.setSettings(convertToSettingEntity(userSettings));
                userRepo.save(entity);
                sendEmailNotificationUpdate(true, entity.getEmail());
            } else if (entity.getSettings().getNotifications().getEmail() && !userSettings.getNotifications().getEmail()) {
                entity.setSettings(convertToSettingEntity(userSettings));
                userRepo.save(entity);
                sendEmailNotificationUpdate(false, entity.getEmail());
            } else {
                if (!entity.getSettings().equals(convertToSettingEntity(userSettings)))
                    sendNotificationUpdateConfirmation(entity.getEmail());
                entity.setSettings(convertToSettingEntity(userSettings));
                userRepo.save(entity);
            }
            return userSettings;
        }
        throw new UserNotFoundException("User Not Found!");
    }

    private void sendNotificationUpdateConfirmation(String userEmail) {
        String htmlBody = "<p>Your settings have been successfully updated.";
        sendEmail(htmlBody, userEmail);
    }

    public void sendEmailNotificationUpdate(boolean active, String userEmail) {
        String htmlBody = active ? "<p> You have chosen to activate email notifications from Scrumptious!\n" :
                "<p> You have unsubscribed from Scrumptious email notifications, we are sad to see you go :(";
        sendEmail(htmlBody, userEmail);
    }

    private void sendEmail(String htmlBody, String userEmail) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userEmail))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBody))).withSubject(new Content()
                                .withCharset("UTF-8").withData("Scrumptious Email Notification Update"))).withSource(emailFrom);
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
        } else {
            log.info("Invalid activation token - " + uuid);
            throw new UserNotFoundException("Token invalid");
        }
    }

    public User getUser(String email) throws UserNotFoundException {
        Optional<UserEntity> entity = userRepo.findByEmail(email);
        if (entity.isPresent()) {
            return convertToDTO(entity.get());
        } else throw new UserNotFoundException("User not found");
    }

    public User getUser(Long id) throws UserNotFoundException {
        Optional<UserEntity> entity = userRepo.findById(id);
        if (entity.isPresent()) {
            return convertToDTO(entity.get());
        } else throw new UserNotFoundException("User not found");
    }

    public void deleteUser(Long id) {
        //TODO delete orders
        userRepo.deleteById(id);
    }

    public User updateProfile(User user) throws UserNotFoundException {
        Optional<UserEntity> entityOptional = userRepo.findById(user.getId());
        if (entityOptional.isPresent()) {
            UserEntity entity = entityOptional.get();
            if (!user.getEmail().equals(entity.getEmail())) {
                entity.setActivated(false);
                entity.setActivationToken(UUID.randomUUID());
                entity.setActivationTokenExpiration(Instant.now().plusMillis(7200000));
                sendActivationEmail(user.getEmail(), entity.getActivationToken(),
                        entity.getUserRole().getRole().equals("admin") ? adminPortalURL : userPortalURL);
                entity.setEmail(user.getEmail());
            }
            UserEntity updateEntity = convertToEntity(user);
            entity.setBirthDate(updateEntity.getBirthDate());
            entity.setPhone(updateEntity.getPhone());
            entity.setFirstName(updateEntity.getFirstName());
            entity.setLastName(updateEntity.getLastName());
            userRepo.save(entity);
            return convertToDTO(entity);
        } else {
            throw new UserNotFoundException("User not found!");
        }
    }

    public UserEntity convertToEntity(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);
        //populate settings, modelMapper cannot get these
        DriverService.convertSettingsToEntity(entity, user.getSettings(), formatter, user.getDOB());
        return entity;
    }

    private User convertToDTO(UserEntity entity) {
        User user = mapper.map(entity, User.class);

        user.setIsVeteran(entity.getVeteran());
        user.setDOB(entity.getBirthDate().format((formatter)));
        user.getSettings().getNotifications().setEmail(entity.getSettings().getNotifications().getEmail());
        user.getSettings().getNotifications().setEmailDelivery(entity.getSettings().getNotifications().getEmailDelivery());
        user.getSettings().getNotifications().setEmailOrder(entity.getSettings().getNotifications().getEmailOrder());
        user.getSettings().getNotifications().setText(entity.getSettings().getNotifications().getPhoneOption());
        user.getSettings().setTheme(entity.getSettings().getThemes().getDark() ? UserSettings.ThemeEnum.DARK : UserSettings.ThemeEnum.LIGHT);

        List<Long> orderIDs = new ArrayList<>();

        //delete password
        user.setPassword(null);
        return user;
    }

    private SettingsEntity convertToSettingEntity(UserSettings userSettings) {
        SettingsEntity entity = mapper.map(userSettings, SettingsEntity.class);
        entity.setThemes(new ThemesEntity().setDark(userSettings.getTheme().equals(UserSettings.ThemeEnum.DARK)));
        entity.setNotifications(entity.getNotifications().setPhoneOption(userSettings.getNotifications().getText()));
        return entity;
    }
}
