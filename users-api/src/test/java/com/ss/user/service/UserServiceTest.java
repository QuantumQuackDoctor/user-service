package com.ss.user.service;

import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.ss.user.errors.ConfirmationTokenExpiredException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @MockBean
    JavaMailSender javaMailSender;
    @Captor
    ArgumentCaptor<MimeMessage> emailCaptor;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup(){
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void insertUser() throws MessagingException {
        when(userRepo.save(userCaptor.capture())).thenReturn(null);

        //create sample user to insert
        User testInsert = createSampleUser();

        //insert sample
        userService.insertUser(testInsert);

        //capture insertion from userRepo
        UserEntity insertedUser = userCaptor.getValue();


        assertNull(insertedUser.getId()); //id should be null upon insertion
        assertEquals(0, insertedUser.getPoints());
        assertEquals("4443324@invalid.com", insertedUser.getEmail());
        assertEquals("firstName", insertedUser.getFirstName());
        assertEquals("lastName", insertedUser.getLastName());
        assertTrue(passwordEncoder.matches("password", insertedUser.getPassword())); //test hashing
        assertEquals(2002, insertedUser.getBirthDate().getYear());
        assertEquals(Month.JULY, insertedUser.getBirthDate().getMonth());
        assertEquals(20, insertedUser.getBirthDate().getDayOfMonth());
        assertFalse(insertedUser.getVeteran());
        assertFalse(insertedUser.getSettings().getNotifications().getEmail());
        assertFalse(insertedUser.getSettings().getNotifications().getPhoneOption());
        assertTrue(insertedUser.getSettings().getThemes().getDark());

        verify(javaMailSender).send(emailCaptor.capture());

        assertEquals("ezra.john.mitchell@gmail.com", emailCaptor.getValue().getFrom()[0].toString());
    }

    User createSampleUser(){
        User user = new User();
        user.setId((long) 234453); //should be overwritten
        user.setEmail("4443324@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("password"); //should be hashed
        user.setDOB("2002-07-20"); //test local date parsing
        user.setPoints(233434); //should be overwritten
        user.setVeteranStatus(false);
        UserSettings settings = new UserSettings();
        settings.setTheme(UserSettings.ThemeEnum.DARK);
        UserSettingsNotifications notifications = new UserSettingsNotifications();
        notifications.setEmail(false);
        notifications.setText(false);
        settings.setNotifications(notifications);
        user.setSettings(settings);
        return user;
    }

    UserEntity createSampleUserEntity(){
        UserEntity user = new UserEntity();
        user.setId((long) 234453); //should be overwritten
        user.setEmail("4443324@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("password"); //should be hashed
        user.setBirthDate(LocalDate.now()); //test local date parsing
        user.setPoints(233434); //should be overwritten
        user.setVeteran(false);
        SettingsEntity settings = new SettingsEntity();
        settings.setThemes(new ThemesEntity().setDark(true));
        NotificationsEntity notificationsEntity = new NotificationsEntity();
        notificationsEntity.setEmail(false);
        notificationsEntity.setPhoneOption(false);
        settings.setNotifications(notificationsEntity);
        user.setSettings(settings);
        return user;
    }

    @Test
    void activateUser_WithValidToken() throws UserNotFoundException, MessagingException, ConfirmationTokenExpiredException {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = createSampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().plusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(userCaptor.capture())).thenReturn(sampleUser);

        userService.activateAccount(token);

        assertTrue(userCaptor.getValue().getActivated());
    }

    @Test
    void activateUser_WithExpiredToken() throws UserNotFoundException, MessagingException {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = createSampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().minusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(userCaptor.capture())).thenReturn(sampleUser);

        try {
            userService.activateAccount(token);
            fail();
        } catch (ConfirmationTokenExpiredException ignored) {
        }

        assertFalse(userCaptor.getValue().getActivated());
    }

    @Test
    void activateUser_withInvalidToken() throws MessagingException, ConfirmationTokenExpiredException {
        UUID token = UUID.randomUUID();
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.empty());

        try{
            userService.activateAccount(token);
            fail();
        }catch (UserNotFoundException ignored){
        }
    }
}