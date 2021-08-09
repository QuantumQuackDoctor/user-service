package com.ss.user.api;

import com.database.security.AuthRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.model.*;
import com.ss.user.service.AuthService;
import com.ss.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = AuthApiController.class)
class AuthApiControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    AuthService authService;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;


    @Test
    void Register_WithValidInput_ShouldReturnSuccess() throws Exception {
        when(userService.emailAvailable("email@invalid.com")).thenReturn(true);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        //create sample user to insert
        User testInsert = createSampleUser();


        mockMvc.perform(put("/accounts/register")
                .content(mapper.writeValueAsString(testInsert))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).insertUser(testInsert, false);
    }

    @Test
    void Register_WithNullProperty_ShouldReturnBadRequest() throws Exception {
        //create sample user to insert
        User testInsert = createSampleUser();

        testInsert.setPassword(null); //invalidate input

        mockMvc.perform(put("/accounts/register")
                .content(mapper.writeValueAsString(testInsert))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).insertUser(testInsert, false); //verify no user was inserted
    }

    @Test
    void Register_WithTakenEmail_ShouldReturnConflict() throws Exception {
        when(userService.emailAvailable("email@invalid.com")).thenReturn(false);
        //create sample user to insert
        User testInsert = createSampleUser();


        mockMvc.perform(put("/accounts/register")
                .content(mapper.writeValueAsString(testInsert))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(0)).insertUser(testInsert, false); //verify no user was inserted
    }

    @Test
    void Register_AsAdminWithCorrectEmail_ShouldReturnOK() throws Exception {
        when(userService.emailAvailable("email@smoothstack.com")).thenReturn(true);
        //create sample user to insert
        User testInsert = createSampleUser();
        testInsert.setEmail("email@smoothstack.com");

        mockMvc.perform(put("/accounts/register?admin=true")
                        .content(mapper.writeValueAsString(testInsert))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).insertUser(testInsert, true); //verify no user was inserted
    }

    @Test
    void Login_WithValidCredentials_ShouldReturnJWT() throws Exception {
        AuthRequest testRequest = createSampleAuthRequest();
        when(authService.authenticate(testRequest)).thenReturn(createSampleAuthResponse());

        mockMvc.perform(post("/accounts/login")
                .content(mapper.writeValueAsString(testRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("jwt").exists())
                .andExpect(status().isOk());
    }

    @Test
    void Login_WithInvalidCredentials_ShouldReturnBadRequest() throws  Exception {
        AuthRequest testRequest = createSampleAuthRequest();
        when(authService.authenticate(testRequest)).thenThrow(new InvalidCredentialsException(""));

        mockMvc.perform(post("/accounts/login")
                .content(mapper.writeValueAsString(testRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private AuthRequest createSampleAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setEmail("email");
        request.setPassword("password");
        request.setIsDriver(false);
        return request;
    }

    private AuthResponse createSampleAuthResponse() {
        return new AuthResponse("sampleJwt");
    }


    private User createSampleUser() {
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