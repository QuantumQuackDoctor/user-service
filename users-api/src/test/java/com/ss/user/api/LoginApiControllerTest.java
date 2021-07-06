package com.ss.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Login Api tests, mocking service
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginApiControllerTest {

    @MockBean(AuthService.class)
    AuthService authService;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Test
    void postLogin() throws Exception {
        //create auth response
        AuthResponse response = new AuthResponse();
        response.setJwt("jwt");

        //create auth request
        AuthRequest request = new AuthRequest();
        request.setEmail("email");
        request.setPassword("password");
        request.setIsDriver(false);

        given(authService.authenticate(request)).willReturn(response);
        //send request
        mockMvc.perform(post("/login")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").value("jwt"));
    }
}