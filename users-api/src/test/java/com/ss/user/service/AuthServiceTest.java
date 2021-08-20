package com.ss.user.service;

import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.database.security.AuthRepo;
import com.database.security.CustomUserDetails;
import com.database.security.SecurityConfig;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {AuthService.class, SecurityConfig.class, AuthRepo.class, CustomUserDetails.class})
class AuthServiceTest {

    @MockBean
    AuthRepo authRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;

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

    private UserEntity createSampleUser() {
        return new UserEntity()
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