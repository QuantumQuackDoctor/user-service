package com.ss.user.api;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ss.user.config.UserDetailsConfig;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserDetailsConfig.class)
class UserApiControllerTest {

    @MockBean
    UserRepo userRepo;

    @MockBean
    AmazonSimpleEmailService emailService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        when(userRepo.findByEmail("email")).thenReturn(Optional.of(createSample()));
        when(userRepo.findById(234453L)).thenReturn(Optional.of(createSample()));
    }

    @Test
    @WithMockUser(username = "email", authorities = "user")
    void getUser_WithUser_ShouldReturnOK() throws Exception {
        mockMvc.perform(get("/accounts/user"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'email': 'email@invalid.com', 'id': 234453, 'DOB': '2002-07-20'}"));
    }

    @Test
    @WithMockUser(username = "doesntExists", authorities = "user")
    void getUser_WithNoUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/accounts/user")).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "email", userDetailsServiceBeanName = "testUserDetailsService")
    void deleteUser_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/accounts/user")).andExpect(status().isOk());
        verify(userRepo).deleteById(1L);
    }

    @Test
    @WithUserDetails(value = "email", userDetailsServiceBeanName = "testUserDetailsService")
    void updateProfile_ShouldReturnOK() throws Exception {
        User sampleDTO = createSampleUserDTO();
        when(userRepo.findById(any())).thenReturn(Optional.of(createSample()));
        sampleDTO.setFirstName("firstNameV2");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        mockMvc.perform(patch("/accounts/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "email", userDetailsServiceBeanName = "testUserDetailsService")
    void updateProfile_ShouldReturnException() throws Exception {
        User sampleDTO = createSampleUserDTO();
        sampleDTO.setId(1L);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        mockMvc.perform(patch("/accounts/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(sampleDTO)))
                .andExpect(status().isNotFound());
    }


    private User createSampleUserDTO() {
        User userDTO = new User();
        userDTO.setId((long) 1);
        userDTO.setEmail("email@invalid.com");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDTO.setPhone("phone");
        userDTO.setPassword("password");
        userDTO.setDOB(LocalDate.parse("2002-07-20").toString());
        userDTO.setPoints(233434);
        userDTO.setIsVeteran(false);
        userDTO.setSettings(UserSettings.builder().notifications(
                UserSettingsNotifications.builder().email(true).text(true).build()).theme(
                UserSettings.ThemeEnum.DARK
        ).build());
        userDTO.setOrders(Collections.emptyList());
        return userDTO;
    }

    private UserEntity createSample() {
        UserEntity user = new UserEntity();
        user.setId((long) 234453);
        user.setEmail("email@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhone("phone");
        user.setPassword("password");
        user.setBirthDate(LocalDate.parse("2002-07-20"));
        user.setPoints(233434);
        user.setVeteran(false);
        user.setSettings(new SettingsEntity().setNotifications(
                new NotificationsEntity().setEmail(true).setPhoneOption(true)).setThemes(
                new ThemesEntity().setDark(true)));
//        user.setOrderList(Collections.emptyList());
        return user;
    }
}