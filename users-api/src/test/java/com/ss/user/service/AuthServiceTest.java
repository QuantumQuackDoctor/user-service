package com.ss.user.service;

import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.ss.user.errors.exceptions.InvalidCredentialsException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceTest {

    @MockBean(UserRepo.class)
    UserRepo userRepo;
    @MockBean(PasswordEncoder.class)
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;

    @Test
    void authenticate() throws InvalidCredentialsException {
        when(this.passwordEncoder.encode("password")).thenReturn("password");
        when(this.passwordEncoder.encode("badPassword")).thenReturn("badPassword");
        UserEntity user1 = new UserEntity().setEmail("email").setPassword("password").setUserRole(new UserRoleEntity().setRole("user"));
        //return fake user
        given(userRepo.findByEmail("email")).willReturn(Optional.ofNullable(user1));

        AuthRequest request = new AuthRequest();
        request.setEmail("email"); //matches the email specified above
        request.setPassword("password"); //also matches

        AuthResponse response = authService.authenticate(request);
        assertNotNull(response.getJwt());

        try {
            request.setEmail("email"); //matches the email specified above
            request.setPassword("badPassword"); //doesn't match user data
            authService.authenticate(request);
            fail(); //should throw InvalidCredentialsException
        } catch (InvalidCredentialsException e) {
        }
    }
}