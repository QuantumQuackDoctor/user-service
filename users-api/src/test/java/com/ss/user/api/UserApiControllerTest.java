package com.ss.user.api;

import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.ss.user.config.UserDetailsConfig;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserDetailsConfig.class)
class UserApiControllerTest {

    @MockBean
    UserRepo userRepo;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        when(userRepo.findByEmail("email")).thenReturn(Optional.of(createSample()));
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
        return user;
    }
}