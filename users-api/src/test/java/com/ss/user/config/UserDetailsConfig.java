package com.ss.user.config;

import com.database.security.AuthDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

@TestConfiguration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService testUserDetailsService() {
        return s -> new AuthDetails(
                (long) 1,
                "email",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("user")));
    }
}
