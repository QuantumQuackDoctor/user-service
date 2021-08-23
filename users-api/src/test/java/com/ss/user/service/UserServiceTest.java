package com.ss.user.service;

import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @MockBean(OrderRepo.class)
    OrderRepo orderRepo;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

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
        testInsert.setOrderList(Collections.emptyList());
        return testInsert;
    }

    @Test
    void insertUser() {
        when(userRepo.save(userCaptor.capture())).thenReturn(null);
        //create sample user to insert
        User testInsert = sampleUser();
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
    }

    @Test
    void updateUserProfile() throws UserNotFoundException {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(sampleUserEntity()));

        User updateSample = sampleUser();
        User updateProfile = userService.updateProfile(updateSample);

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
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(new User()));
    }
}