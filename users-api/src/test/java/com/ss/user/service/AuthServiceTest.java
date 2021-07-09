package com.ss.user.service;

import com.database.ormlibrary.user.UserEntity;
import com.database.ormlibrary.user.UserRoleEntity;
import com.ss.user.errors.InvalidCredentialsException;
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

    @MockBean
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;

    @Test
    void Authenticate_withValidCredentials_ShouldReturnJWT() throws InvalidCredentialsException {
        //return fake user
        given(userRepo.findByEmail("email")).willReturn(Optional.ofNullable(createSampleUser()));

        AuthRequest request = createSampleAuthRequest();
        AuthResponse response = authService.authenticate(request);
        assertNotNull(response.getJwt());
    }

    @Test
    void Authenticate_withInvalidPassword_ShouldThrowInvalidCredentials(){
        given(userRepo.findByEmail("email")).willReturn(Optional.ofNullable(createSampleUser()));

        AuthRequest request = createSampleAuthRequest();
        request.setPassword("invalid password");

        try{
            authService.authenticate(request);
            fail();
        }catch (InvalidCredentialsException ignored){
            //exception thrown
        }
    }

    @Test
    void Authenticate_WithInvalidUser_ShouldThrowInvalidCredentials(){
        given(userRepo.findByEmail("email")).willReturn(Optional.empty());

        AuthRequest request = createSampleAuthRequest();

        try{
            authService.authenticate(request);
            fail();
        }catch (InvalidCredentialsException ignored){
            //exception thrown
        }
    }

    private UserEntity createSampleUser(){
        return new UserEntity().setEmail("email").setPassword(passwordEncoder.encode("password")).setUserRole(new UserRoleEntity().setRole("user"));
    }

    private AuthRequest createSampleAuthRequest(){
        AuthRequest request = new AuthRequest();
        request.setEmail("email"); //matches the email specified above
        request.setPassword("password"); //also matches
        return request;
    }
}