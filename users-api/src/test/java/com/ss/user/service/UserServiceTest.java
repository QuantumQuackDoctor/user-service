package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.database.ormlibrary.user.*;
import com.ss.user.errors.ConfirmationTokenExpiredException;
import com.ss.user.errors.EmailTakenException;
import com.ss.user.errors.InvalidAdminEmailException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.OrderRepo;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @MockBean(OrderRepo.class)
    OrderRepo orderRepo;
    @MockBean
    AmazonSimpleEmailService emailSender;
    @Captor
    ArgumentCaptor<SendEmailRequest> emailCaptor;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Value("${email.sender}")
    String emailFrom;

    User sampleUser() {
        //create sample user to insert
        User testInsert = new User();
        testInsert.setId((long) 234453); //should be overwritten
        testInsert.setEmail("4443324@invalid.com");
        testInsert.setPhone("1234567890");
        testInsert.setFirstName("firstName");
        testInsert.setLastName("lastName");
        testInsert.setPassword("password"); //should be hashed
        testInsert.setDOB("2002-07-20"); //test local date parsing
        testInsert.setPoints(233434); //should be overwritten
        testInsert.setIsVeteran(false);
        UserSettings settings = new UserSettings();
        settings.setTheme(UserSettings.ThemeEnum.DARK);
        UserSettingsNotifications notifications = new UserSettingsNotifications();
        notifications.setEmail(false);
        notifications.setText(false);
        settings.setNotifications(notifications);
        testInsert.setSettings(settings);
        testInsert.setOrders(Collections.emptyList());
        return testInsert;
    }

    UserEntity sampleUserEntity() {
        UserEntity testInsert = new UserEntity();
        testInsert.setId((long) 234453);
        testInsert.setEmail("TestingEmail@invalid.com");
        testInsert.setPhone("09786354321");
        testInsert.setFirstName("SampleFirst");
        testInsert.setLastName("SampleLast");
        testInsert.setPassword("password");
        testInsert.setBirthDate(LocalDate.parse("1860-08-21"));
        testInsert.setPoints(233434);
        testInsert.setVeteran(false);
        SettingsEntity settings = new SettingsEntity();
        settings.setThemes(new ThemesEntity().setDark(true));
        NotificationsEntity notifications = new NotificationsEntity();
        notifications.setEmail(true);
        notifications.setPhoneOption(true);
        settings.setNotifications(notifications);
        testInsert.setSettings(settings);
        testInsert.setUserRole(new UserRoleEntity().setRole("admin"));
        return testInsert;
    }

    @Test
    void insertUser() throws InvalidAdminEmailException, EmailTakenException {
        when(userRepo.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArguments()[0]);
        //create sample user to insert
        User testInsert = sampleUser();
        //insert sample
        userService.insertUser(testInsert, false);

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

        verify(emailSender).sendEmail(emailCaptor.capture());

        assertEquals(emailFrom, emailCaptor.getValue().getSource());
    }

    @Test
    void updateUserProfile() throws UserNotFoundException {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(sampleUserEntity()));

        User updateSample = sampleUser();
        User updateProfile = userService.updateProfile(updateSample, false);

        assertEquals(233434, updateProfile.getPoints());
        assertEquals("4443324@invalid.com", updateProfile.getEmail());
        assertEquals("firstName", updateProfile.getFirstName());
        assertEquals("lastName", updateProfile.getLastName());
        LocalDate dob = LocalDate.parse(updateProfile.getDOB());
        assertEquals(2002, dob.getYear());
        assertEquals(Month.JULY, dob.getMonth());
        assertEquals(20, dob.getDayOfMonth());
    }

    @Test
    void updateUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(new User(), false));
    }

    @Test
    void insertAdmin() throws InvalidAdminEmailException, EmailTakenException {
        when(userRepo.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //create sample user to insert
        User testInsert = sampleUser();

        testInsert.setEmail("email@smoothstack.com");
        //insert sample
        userService.insertUser(testInsert, true);

        //capture insertion from userRepo
        UserEntity insertedUser = userCaptor.getValue();


        assertNull(insertedUser.getId()); //id should be null upon insertion
        assertEquals(0, insertedUser.getPoints());
        assertEquals("email@smoothstack.com", insertedUser.getEmail());
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

        verify(emailSender).sendEmail(emailCaptor.capture());

        assertEquals(emailFrom, emailCaptor.getValue().getSource());
    }

    @Test
    void insertInvalidAdmin() {
        when(userRepo.save(userCaptor.capture())).thenReturn(null);

        //create sample user to insert
        User testInsert = sampleUser();

        testInsert.setEmail("email@notsmoothstack.com");
        try {
            //insert sample
            userService.insertUser(testInsert, true);
            fail();
        } catch (InvalidAdminEmailException | EmailTakenException ignored) {
        }
    }

    @Test
    void activateUser_WithValidToken() throws UserNotFoundException, ConfirmationTokenExpiredException {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = sampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().plusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(userCaptor.capture())).thenReturn(sampleUser);

        userService.activateAccount(token);

        assertTrue(userCaptor.getValue().getActivated());
    }

    @Test
    void activateUser_WithExpiredToken() throws UserNotFoundException {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = sampleUserEntity();
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
    void activateUser_withInvalidToken() throws ConfirmationTokenExpiredException {
        UUID token = UUID.randomUUID();
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.empty());

        try {
            userService.activateAccount(token);
            fail();
        } catch (UserNotFoundException ignored) {
        }
    }


    @Test
    void getUser_WithValidUserId() throws UserNotFoundException {
        UserEntity entity = sampleUserEntity();
        when(userRepo.findById(1L)).thenReturn(Optional.of(entity));

        User user = userService.getUser(1L);
        assertEquals(entity.getEmail(), user.getEmail());
        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getFirstName(), user.getFirstName());
        assertEquals(entity.getLastName(), user.getLastName());
        assertEquals(entity.getPhone(), user.getPhone());
        assertNull(user.getPassword());
    }

    @Test
    void getUser_WithInvalidUserId() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(1L);
        });
    }

    @Test
    void getUser_WithValidUserEmail() throws UserNotFoundException {
        UserEntity entity = sampleUserEntity();
        when(userRepo.findByEmail("email")).thenReturn(Optional.of(entity));

        User user = userService.getUser("email");
        assertEquals(entity.getEmail(), user.getEmail());
        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getFirstName(), user.getFirstName());
        assertEquals(entity.getLastName(), user.getLastName());
        assertEquals(entity.getPhone(), user.getPhone());
        assertNull(user.getPassword());
    }

    @Test
    void getUser_WithInvalidUserEmail() {
        when(userRepo.findByEmail("email")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser("email");
        });
    }

    @Test
    void deleteUser_WithUser_DeactivatesAccount() throws UserNotFoundException {
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        UserEntity originalEntity = sampleUserEntity();
        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleUserEntity()));
        userService.deleteUser(1L);
        verify(userRepo, times(1)).save(captor.capture());

        UserEntity mutatedEntity = captor.getValue();

        assertFalse(mutatedEntity.getActivated());

        assertEquals(originalEntity.getEmail(), mutatedEntity.getDeactivatedEmail());
    }

    @Test
    void deleteUser_WithoutUser_ThrowsNotFound(){
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

}