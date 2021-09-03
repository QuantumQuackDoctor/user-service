package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.user.UserEntity;
import com.ss.user.model.Driver;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.DriverRepo;
import com.ss.user.repo.OrderRepo;
import com.ss.user.repo.UserRepo;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class DriverServiceTest {
    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @MockBean(DriverRepo.class)
    DriverRepo driverRepo;
    @MockBean(OrderRepo.class)
    OrderRepo orderRepo;
    @Captor
    ArgumentCaptor<DriverEntity> driverCaptor;
    @Autowired
    UserService userService;
    @Autowired
    DriverService driverService;
    @Autowired
    PasswordEncoder passwordEncoder;


    private Driver createSampleDriverDTO() {
        Driver driver = new Driver();
        driver.setId(null);
        driver.setFirstName("first");
        driver.setLastName("last");
        driver.setDOB("2002-07-20");
        driver.setPassword("password");
        driver.setPhone("5555555555");
        driver.setCar("big car");
        driver.setSettings(createUserSettings());
        driver.setEmail("driver@email.com");

        return driver;
    }

    private UserSettings createUserSettings() {
        UserSettings settings = new UserSettings();
        settings.setTheme(UserSettings.ThemeEnum.LIGHT);
        UserSettingsNotifications notifications = new UserSettingsNotifications();
        notifications.setText(false);
        notifications.setEmail(false);
        settings.setNotifications(notifications);
        return settings;
    }
}
