package com.ss.user.api;

import com.database.ormlibrary.user.NotificationsEntity;
import com.database.ormlibrary.user.SettingsEntity;
import com.database.ormlibrary.user.ThemesEntity;
import com.database.ormlibrary.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.model.UserSettingsNotifications;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {

    @MockBean
    UserRepo userRepo;

    @Captor
    ArgumentCaptor<UserEntity> userCaptor;

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        when(userRepo.existsByEmail("email@invalid.com")).thenReturn(false);
        when(userRepo.existsByEmail("exists@invalid.com")).thenReturn(true);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
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

    @Test
    void adminRegister_WithInvalidEmail() throws Exception {

        User testInsert = createSample();
        testInsert.setEmail("email@notsmoothstack.com");

        mockMvc.perform(put("/accounts/register?admin=true")
                        .content(mapper.writeValueAsString(testInsert))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void activateIntegration_withValidToken() throws Exception {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = createSampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().plusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(userCaptor.capture())).thenReturn(sampleUser);

        mockMvc.perform(post("/accounts/activate/" + token)).andExpect(status().isOk());

        assertTrue(userCaptor.getValue().getActivated());
    }

    @Test
    void activateIntegration_withExpiredToken() throws Exception {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = createSampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().minusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(userCaptor.capture())).thenReturn(sampleUser);

        mockMvc.perform(post("/accounts/activate/" + token)).andExpect(status().isGone());

        assertFalse(userCaptor.getValue().getActivated());
    }

    @Test
    void activateIntegration_withInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();
        UserEntity sampleUser = createSampleUserEntity();
        sampleUser.setActivationToken(token);
        sampleUser.setActivationTokenExpiration(Instant.now().minusMillis(1000));
        when(userRepo.findByActivationToken(token)).thenReturn(Optional.empty());

        mockMvc.perform(post("/accounts/activate/" + token)).andExpect(status().isNotFound());

        verify(userRepo, times(0)).save(userCaptor.capture());
    }

    UserEntity createSampleUserEntity() {
        UserEntity user = new UserEntity();
        user.setId((long) 234453); //should be overwritten
        user.setEmail("4443324@invalid.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("password"); //should be hashed
        user.setBirthDate(LocalDate.now()); //test local date parsing
        user.setPoints(233434); //should be overwritten
        user.setVeteran(false);
        SettingsEntity settings = new SettingsEntity();
        settings.setThemes(new ThemesEntity().setDark(true));
        NotificationsEntity notificationsEntity = new NotificationsEntity();
        notificationsEntity.setEmail(false);
        notificationsEntity.setPhoneOption(false);
        settings.setNotifications(notificationsEntity);
        user.setSettings(settings);
        return user;
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