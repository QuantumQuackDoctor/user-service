package com.ss.user.api;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.ss.user.config.UserDetailsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserDetailsConfig.class)
@Sql("classpath:users.sql")
class UsersApiControllerTest {

    @MockBean
    AmazonSimpleEmailService emailService;

    @Autowired
    MockMvc mockMvc;


    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void getUser_WithValidId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/accounts/users?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'email':'email', 'id': 1}"));
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void getUser_WithInvalidId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/accounts/users?id=0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void getUser_WithValidEmail_ReturnsUser() throws Exception {
        mockMvc.perform(get("/accounts/users?email=email"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'email':'email', 'id': 1}"));
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsService")
    void getUser_WithInvalidEmail_ReturnsUser() throws Exception {
        mockMvc.perform(get("/accounts/users?email=doesntexist"))
                .andExpect(status().isNotFound());
    }
}