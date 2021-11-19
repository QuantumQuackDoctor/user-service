package com.ss.user.api;

import com.database.ormlibrary.driver.DriverEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.ss.user.config.UserDetailsConfig;
import com.ss.user.model.Driver;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.DriverRepo;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserDetailsConfig.class)
class DriverApiControllerTest {

    @Autowired
    DriverRepo driverRepo;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserRoleRepo userRoleRepo;

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
        Long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();

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

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void readDriver_ShouldReturnDriver() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setEmail("readDriverShouldReturnDriver@example.com");
        //insert driver
        MvcResult result = mockMvc.perform(put("/accounts/driver")
                        .content(mapper.writeValueAsString(driverDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();

        mockMvc.perform(get("/accounts/driver/" + id))
                .andExpect(status().isOk());

        driverRepo.deleteById(id);
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void readDriver_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/accounts/driver/" + 203982))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void updateDriver_WithoutPassword_ShouldUpdateDriver() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setEmail("updateDriverNoPassword@example.com");
        //insert driver
        MvcResult result = mockMvc.perform(put("/accounts/driver")
                        .content(mapper.writeValueAsString(driverDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();

        driverDTO.setId(id);
        driverDTO.setCar("new Car");
        driverDTO.setFirstName("newFirstName");
        driverDTO.setLastName("newLastName");
        driverDTO.setPassword("newPassword1#");

        mockMvc.perform(post("/accounts/driver?update-password=false").content(mapper.writeValueAsString(driverDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<DriverEntity> updatedDriverOptional = driverRepo.findById(id);

        assertTrue(updatedDriverOptional.isPresent());
        DriverEntity updatedDriver = updatedDriverOptional.get();

        assertEquals(driverDTO.getCar(), updatedDriver.getCar());
        assertEquals(driverDTO.getFirstName(), updatedDriver.getUser().getFirstName());
        assertEquals(driverDTO.getLastName(), updatedDriver.getUser().getLastName());
        assertFalse(passwordEncoder.matches("newPassword", updatedDriver.getUser().getPassword()));

        driverRepo.deleteById(id);
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void updateDriver_WithPassword_ShouldUpdateDriver() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setEmail("updateDriverWithPassword@example.com");
        //insert driver
        MvcResult result = mockMvc.perform(put("/accounts/driver")
                        .content(mapper.writeValueAsString(driverDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();

        driverDTO.setId(id);
        driverDTO.setCar("new Car");
        driverDTO.setFirstName("newFirstName");
        driverDTO.setLastName("newLastName");
        driverDTO.setPassword("newPassword1#");

        mockMvc.perform(post("/accounts/driver?update-password=true").content(mapper.writeValueAsString(driverDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<DriverEntity> updatedDriverOptional = driverRepo.findById(id);

        assertTrue(updatedDriverOptional.isPresent());
        DriverEntity updatedDriver = updatedDriverOptional.get();

        assertEquals(driverDTO.getCar(), updatedDriver.getCar());
        assertEquals(driverDTO.getFirstName(), updatedDriver.getUser().getFirstName());
        assertEquals(driverDTO.getLastName(), updatedDriver.getUser().getLastName());
        assertTrue(passwordEncoder.matches("newPassword1#", updatedDriver.getUser().getPassword()));

        driverRepo.deleteById(id);
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void updateDriver_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setId(279037493727L);

        mockMvc.perform(post("/accounts/driver?update-password=true").content(mapper.writeValueAsString(driverDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void updateDriver_WithNullId_ShouldReturnNotFound() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setId(null);

        mockMvc.perform(post("/accounts/driver?update-password=true").content(mapper.writeValueAsString(driverDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "username", authorities = "admin")
    void deleteDriver_ShouldDeleteDriver() throws Exception {
        Driver driverDTO = createSampleDriverDTO();
        driverDTO.setEmail("deleteDriver@example.com");
        //insert driver
        MvcResult result = mockMvc.perform(put("/accounts/driver")
                        .content(mapper.writeValueAsString(driverDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "id")).longValue();


        mockMvc.perform(delete("/accounts/driver/" + id)).andExpect(status().isOk());

        Optional<DriverEntity> updatedDriverOptional = driverRepo.findById(id);

        assertFalse(updatedDriverOptional.isPresent());
    }

    @Test
    @Sql({"classpath:driver-data.sql"})
    @WithUserDetails(value = "driver", userDetailsServiceBeanName = "testUserDetailsService")
    void checkIn_ShouldSetStatusToTrue() throws Exception {
        mockMvc.perform(post("/accounts/driver/checkin")).andExpect(status().isOk());

        assertTrue(driverRepo.findById(1L).get().isCheckedIn());
    }


    @Test
    @Sql({"classpath:driver-data.sql"})
    @WithUserDetails(value = "driver", userDetailsServiceBeanName = "testUserDetailsService")
    void checkOut_ShouldSetStatusToFalse() throws Exception {
        mockMvc.perform(post("/accounts/driver/checkout")).andExpect(status().isOk());

        assertFalse(driverRepo.findById(1L).get().isCheckedIn());
    }

    @Test
    @WithUserDetails(value = "driver", userDetailsServiceBeanName = "testUserDetailsService")
    void checkOut_WithoutUserShouldThrow() throws Exception {
        if (driverRepo.findById(1L).isPresent())
            driverRepo.deleteById(1L);
        mockMvc.perform(post("/accounts/driver/checkout")).andExpect(status().isNotFound());
    }

    private Driver createSampleDriverDTO() {
        Driver driver = new Driver();
        driver.setId(null);
        driver.setFirstName("first");
        driver.setLastName("last");
        driver.setDOB("2002-07-20");
        driver.setPassword("StrongPassword1#");
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
//        Optional<UserRoleEntity> role = userRoleRepo.findByRole("driver");
//        if(role.isPresent())
//            user.setUserRole(role.get());
//        else
//            user.setUserRole(userRoleRepo.save(new UserRoleEntity().setRole("driver")));
//
//        return user;
//    }
}