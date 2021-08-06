package com.ss.user.service;

import com.database.ormlibrary.user.*;
import com.ss.user.errors.ConfirmationTokenExpiredException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    private final JavaMailSender javaMailSender;
    @Value("${user-portal-url}")
    private String userPortalURL;

    public UserService(UserRepo userRepo, UserRoleRepo userRoleRepo, ModelMapper mapper, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    public boolean emailAvailable(String email) {
        return !userRepo.existsByEmail(email);
    }

    public void insertUser(User user) throws MessagingException {
        UserEntity toInsert = convertToEntity(user);
        //set defaults
        toInsert.setId(null);
        toInsert.setPoints(0);
        toInsert.setActivated(false);
        toInsert.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<UserRoleEntity> role;
        if (!(role = userRoleRepo.findByRole("user")).isPresent()) {
            role = Optional.of(userRoleRepo.save(new UserRoleEntity().setRole("user")));
        }
        toInsert.setUserRole(role.get());

        //create activation token
        toInsert.setActivationToken(UUID.randomUUID());
        toInsert.setActivationTokenExpiration(Instant.now().plusMillis(7200000));

        sendActivationEmail(toInsert.getEmail(), toInsert.getActivationToken());

        userRepo.save(toInsert);
    }

    private void sendActivationEmail(String recipient, UUID uuid) throws MessagingException {
        MimeMessage confirmationEmail = javaMailSender.createMimeMessage();

        confirmationEmail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        confirmationEmail.setFrom("ezra.john.mitchell@gmail.com");
        confirmationEmail.setContent("<h3 style=\"background-color: black\">Activate your account</h3>" +
                "Follow this link to activate your account " +
                String.format("<a href=\"%s\">Activate</a>", userPortalURL + "/activate/" + uuid.toString()), "text/html");
        confirmationEmail.setSubject("Scrumptious account activation");

        javaMailSender.send(confirmationEmail);
    }

    public void activateAccount(UUID uuid) throws MessagingException, ConfirmationTokenExpiredException, UserNotFoundException {
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

                sendActivationEmail(userToActivate.getEmail(), userToActivate.getActivationToken());
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
        UserSettings userSettings = user.getSettings();
        SettingsEntity settings = new SettingsEntity();
        settings.setNotifications(new NotificationsEntity()
                .setEmail(userSettings.getNotifications().getEmail())
                .setPhoneOption(userSettings.getNotifications().getText()));
        settings.setThemes(new ThemesEntity().setDark(userSettings.getTheme().equals(UserSettings.ThemeEnum.DARK)));

        entity.setBirthDate(LocalDate.from(formatter.parse(user.getDOB())));
        entity.setSettings(settings);
        return entity;
    }

    private User convertToDTO(UserEntity entity) {
        User user = mapper.map(entity, User.class);
        user.setDOB(entity.getBirthDate().format((formatter)));
        user.getSettings().getNotifications().setEmail(entity.getSettings().getNotifications().getEmail());
        user.getSettings().getNotifications().setText(entity.getSettings().getNotifications().getPhoneOption());
        user.getSettings().setTheme(entity.getSettings().getThemes().getDark() ? UserSettings.ThemeEnum.DARK : UserSettings.ThemeEnum.LIGHT);

        //delete password
        user.setPassword(null);
        return user;
    }
}
