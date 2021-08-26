package com.ss.user.api;

import com.database.ormlibrary.driver.DriverEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.ss.user.model.Driver;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.DriverRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DriverApiControllerTest {

    @Autowired
    DriverRepo driverRepo;
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();


    @Test
    @WithMockUser(username = "doesnotexist", authorities = "admin")
    void createDriver_WithValidData_InsertsDriverAndUser() throws Exception {

        Driver driverDTO = createSampleDriverDTO();

        MvcResult result = mockMvc.perform(put("/accounts/driver")
                .content(mapper.writeValueAsString(driverDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long id = ((Number)JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();

        Optional<DriverEntity> driverEntityOptional = driverRepo.findById(id);

        assertTrue(driverEntityOptional.isPresent());

        DriverEntity driverEntity = driverEntityOptional.get();
        assertEquals(driverEntity.getCar(), driverDTO.getCar());

        assertEquals(driverEntity.getUser().getEmail(), driverDTO.getEmail());
        assertTrue(driverEntity.getUser().getActivated());
        assertEquals(driverEntity.getUser().getFirstName(), driverDTO.getFirstName());
        assertEquals(driverEntity.getUser().getLastName(), driverDTO.getLastName());
        assertEquals(driverEntity.getUser().getBirthDate(), LocalDate.parse(driverDTO.getDOB()));
        assertEquals(driverEntity.getUser().getSettings().getNotifications().getEmail(), driverDTO.getSettings().getNotifications().getEmail());
        assertEquals(driverEntity.getUser().getSettings().getNotifications().getPhoneOption(), driverDTO.getSettings().getNotifications().getText());
        assertFalse(driverEntity.getUser().getSettings().getThemes().getDark());
    }


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

//    private DriverEntity createSampleDriverEntity(){
//        DriverEntity driver = new DriverEntity();
//        driver.setUser(createSampleUserEntity());
//        driver.setCar("big car");
//        driver.setRatings(Collections.singletonList(new DriverRatingEntity()));
//
//
//        return driver;
//    }
//
//    private UserEntity createSampleUserEntity() {
//        UserEntity user = new UserEntity();
//        user.setId(1L);
//        user.setEmail("email@invalid.com");
//        user.setFirstName("firstName");
//        user.setLastName("lastName");
//        user.setPhone("phone");
//        user.setPassword("password");
//        user.setBirthDate(LocalDate.parse("2002-07-20"));
//        user.setSettings(new SettingsEntity().setNotifications(
//                new NotificationsEntity().setEmail(true).setPhoneOption(true)).setThemes(
//                new ThemesEntity().setDark(true)));
//        return user;
//    }
}