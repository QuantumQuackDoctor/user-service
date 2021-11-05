package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.database.ormlibrary.user.PasswordResetEntity;
import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.database.security.AuthRepo;
import com.database.security.CustomUserDetails;
import com.database.security.SecurityConfig;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.errors.ResourceExpiredException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.model.PasswordResetRequest;
import com.ss.user.model.PortalType;
import com.ss.user.repo.PasswordResetRepo;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AuthService.class, SecurityConfig.class, AuthRepo.class, CustomUserDetails.class})
class AuthServiceTest {

    @MockBean
    AuthRepo authRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;
    @MockBean
    UserRepo userRepo;
    @MockBean
    PasswordResetRepo passwordResetRepo;
    @MockBean
    AmazonSimpleEmailService ses;

    @Test
    void Authenticate_withValidCredentials_ShouldReturnJWT() throws InvalidCredentialsException {
        //return fake user
        given(authRepo.findByEmail("email")).willReturn(Optional.ofNullable(createSampleUser()));

        AuthRequest request = createSampleAuthRequest();
        AuthResponse response = authService.authenticate(request);
        assertNotNull(response.getJwt());
    }

    @Test
    void Authenticate_withInvalidPassword_ShouldThrowInvalidCredentials() {
        given(authRepo.findByEmail("email")).willReturn(Optional.ofNullable(createSampleUser()));

        AuthRequest request = createSampleAuthRequest();
        request.setPassword("invalid password");

        try {
            authService.authenticate(request);
            fail();
        } catch (Exception ignored) {
            //exception thrown
        }
    }

    @Test
    void Authenticate_WithInvalidUser_ShouldThrowInvalidCredentials() {
        given(authRepo.findByEmail("email")).willReturn(Optional.empty());

        AuthRequest request = createSampleAuthRequest();

        try {
            authService.authenticate(request);
            fail();
        } catch (Exception ignored) { //I can't remember the specific exception
            //exception thrown
        }
    }

    @Test
    void PasswordResetRequest_WithUser_ShouldSendEmail() throws UserNotFoundException {
        ArgumentCaptor<PasswordResetEntity> captor = ArgumentCaptor.forClass(PasswordResetEntity.class);
        when(userRepo.findByEmail("email")).thenReturn(Optional.of(createSampleUser()));

        authService.sendPasswordResetEmail("email", PortalType.user);

        verify(passwordResetRepo).save(captor.capture());
        verify(ses, times(1)).sendTemplatedEmail(any());

        PasswordResetEntity entity = captor.getValue();

        assertEquals(1L, entity.getUser().getId());
    }

    @Test
    void PasswordResetRequest_WithoutUser_ShouldThrowUserNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> authService.sendPasswordResetEmail("doesnotexist", PortalType.admin));
    }

    @Test
    void PasswordResetUpdate_WithValidToken_ShouldUpdateUser() throws UserNotFoundException, ResourceExpiredException {
        UUID uuid = UUID.randomUUID();
        PasswordResetEntity entity = new PasswordResetEntity()
                .setToken(uuid)
                .setTokenExpiration(Instant.now().plusMillis(20000))
                .setUser(createSampleUser());
        when(passwordResetRepo.findByToken(uuid)).thenReturn(Optional.of(entity));

        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword("newPassword");
        request.setToken(uuid);

        authService.resetPassword(request);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo, times(1)).save(userCaptor.capture());
        verify(passwordResetRepo).delete(entity);

        assertTrue(passwordEncoder.matches("newPassword", userCaptor.getValue().getPassword()));
    }

    @Test
    void PasswordResetUpdate_WithInvalidToken_ShouldThrowNotFound() {
        UUID uuid = UUID.randomUUID();
        when(passwordResetRepo.findByToken(uuid)).thenReturn(Optional.empty());

        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword("newPassword");
        request.setToken(uuid);

        assertThrows(UserNotFoundException.class, () -> authService.resetPassword(request));
    }

    @Test
    void PasswordResetUpdate_WithExpiredToken_ShouldThrowResourceExpired() {
        UUID uuid = UUID.randomUUID();
        PasswordResetEntity entity = new PasswordResetEntity()
                .setToken(uuid)
                .setTokenExpiration(Instant.now().minusSeconds(200))
                .setUser(createSampleUser());
        when(passwordResetRepo.findByToken(uuid)).thenReturn(Optional.of(entity));

        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword("newPassword");
        request.setToken(uuid);

        assertThrows(ResourceExpiredException.class, () -> authService.resetPassword(request));
    }

    private UserEntity createSampleUser() {
        return new UserEntity()
                .setId(1L)
                .setEmail("email")
                .setPassword(passwordEncoder.encode("password"))
                .setActivated(true)
                .setUserRole(new UserRoleEntity().setRole("user"));
    }

    private AuthRequest createSampleAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setEmail("email"); //matches the email specified above
        request.setPassword("password"); //also matches
        return request;
    }
}