package com.ss.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterApiController.class)
class RegisterApiControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Test
    void putRegister() throws Exception {
        when(userService.emailValid("email@invalid.com")).thenReturn(true);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        //create sample user to insert
        User testInsert = createSample();


        mockMvc.perform(put("/register")
                .content(mapper.writeValueAsString(testInsert))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).insertUser(testInsert);

        testInsert.setPassword(null);

        mockMvc.perform(put("/register")
                .content(mapper.writeValueAsString(testInsert))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).insertUser(testInsert);
    }


    private User createSample() {
        User user = new User();
        user.setId((long) 234453);
        user.setEmail("email@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhone("phone");
        user.setPassword("password");
        user.setDOB("2002-07-20");
        user.setPoints(233434);
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
}