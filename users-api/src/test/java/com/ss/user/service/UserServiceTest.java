package com.ss.user.service;

import com.database.ormlibrary.user.UserEntity;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void insertUser() {
        when(userRepo.save(userCaptor.capture())).thenReturn(null);

        //create sample user to insert
        User testInsert = new User();
        testInsert.setId((long) 234453); //should be overwritten
        testInsert.setEmail("4443324@invalid.com");
        testInsert.setFirstName("firstName");
        testInsert.setLastName("lastName");
        testInsert.setPassword("password"); //should be hashed
        testInsert.setDOB("2002-07-20"); //test local date parsing
        testInsert.setPoints(233434); //should be overwritten
        testInsert.setVeteranStatus(false);
        UserSettings settings = new UserSettings();
        settings.setTheme(UserSettings.ThemeEnum.DARK);
        UserSettingsNotifications notifications = new UserSettingsNotifications();
        notifications.setEmail(false);
        notifications.setText(false);
        settings.setNotifications(notifications);
        testInsert.setSettings(settings);

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
}