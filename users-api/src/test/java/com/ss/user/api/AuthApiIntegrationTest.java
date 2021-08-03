package com.ss.user.api;

import com.database.ormlibrary.user.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {

    @MockBean
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        when(userRepo.existsByEmail("email@invalid.com")).thenReturn(false);
        when(userRepo.existsByEmail("exists@invalid.com")).thenReturn(true);
    }

    @Test
    void registerIntegration() throws Exception {
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        //create sample user to insert
        User testInsert = createSample();

        mockMvc.perform(put("/accounts/register")
                        .content(mapper.writeValueAsString(testInsert))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userRepo, times(1)).save(userCaptor.capture());

        UserEntity inserted = userCaptor.getValue();

        assertEquals(testInsert.getEmail(), inserted.getEmail());
        assertEquals(testInsert.getFirstName(), inserted.getFirstName());
        assertEquals(testInsert.getLastName(), inserted.getLastName());
        assertEquals(testInsert.getPhone(), inserted.getPhone());
        assertEquals(testInsert.getPhone(), inserted.getPhone());
        assertEquals(2002, inserted.getBirthDate().getYear());
        assertEquals(20, inserted.getBirthDate().getDayOfMonth());
        assertEquals(Month.JULY, inserted.getBirthDate().getMonth());
        assertEquals(0, inserted.getPoints());
        assertSame(testInsert.getVeteranStatus(), inserted.getVeteran());
        assertNull(inserted.getId());
        assertTrue(passwordEncoder.matches(testInsert.getPassword(), inserted.getPassword()));
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