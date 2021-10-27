package com.ss.user.service;

import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.Driver;
import com.ss.user.repo.DriverRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class DriverServiceTest {
    @MockBean
    DriverRepo driverRepo;
    @Autowired
    DriverService driverService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        DriverEntity sampleEntity = createSampleDriverEntity();
        sampleEntity.setId(1L);
        when(driverRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
    }

    @Test
    void updateDriver_WithoutPassword() throws UserNotFoundException {
        Driver dto = driverService.convertToDTO(createSampleDriverEntity());
        dto.setId(1L);

        String newCar = "different value";
        dto.setCar(newCar);
        String newPassword = "different password";
        dto.setPassword(newPassword);

        ArgumentCaptor<DriverEntity> driverEntityArgumentCaptor = ArgumentCaptor.forClass(DriverEntity.class);
        when(driverRepo.save(driverEntityArgumentCaptor.capture())).thenReturn(createSampleDriverEntity());

        driverService.updateDriver(dto, false);

        DriverEntity driverEntity = driverEntityArgumentCaptor.getValue();
        assertEquals(driverEntity.getCar(), newCar);
        assertNotEquals(driverEntity.getUser().getPassword(), newPassword);
    }

    @Test
    void updateDriver_WithPassword() throws UserNotFoundException {
        Driver dto = driverService.convertToDTO(createSampleDriverEntity());
        dto.setId(1L);

        String newCar = "different value";
        dto.setCar(newCar);
        String newPassword = "different password";
        dto.setPassword(newPassword);

        ArgumentCaptor<DriverEntity> driverEntityArgumentCaptor = ArgumentCaptor.forClass(DriverEntity.class);
        when(driverRepo.save(driverEntityArgumentCaptor.capture())).thenReturn(createSampleDriverEntity());

        driverService.updateDriver(dto, true);

        DriverEntity driverEntity = driverEntityArgumentCaptor.getValue();
        assertEquals(driverEntity.getCar(), newCar);
        assertTrue(passwordEncoder.matches(newPassword, driverEntity.getUser().getPassword()));
    }

    @Test
    void updateDriver_WithInvalidId() throws UserNotFoundException {

        Driver dto = driverService.convertToDTO(createSampleDriverEntity());
        dto.setId(5L);

        String newCar = "different value";
        dto.setCar(newCar);
        String newPassword = "different password";
        dto.setPassword(newPassword);

        assertThrows(UserNotFoundException.class, () -> driverService.updateDriver(dto, true));
    }

    private DriverEntity createSampleDriverEntity() {
        DriverEntity driver = new DriverEntity();
        driver.setUser(createSampleUserEntity());
        driver.setCar("big car");
        return driver;
    }

    private UserEntity createSampleUserEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("email@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhone("phone");
        user.setPassword("password");
        user.setBirthDate(LocalDate.parse("2002-07-20"));
        user.setSettings(new SettingsEntity().setNotifications(
                new NotificationsEntity().setEmail(true).setPhoneOption(true)).setThemes(
                new ThemesEntity().setDark(true)));
        return user;
    }
}