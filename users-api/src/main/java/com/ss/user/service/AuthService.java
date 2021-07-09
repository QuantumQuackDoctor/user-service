package com.ss.user.service;

import com.database.security.AuthDetails;
import com.database.security.JwtTokenUtil;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest req) throws InvalidCredentialsException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(), req.getPassword()
                )
        );
        //create jwt
        if (authentication.isAuthenticated()) {
            return new AuthResponse(jwtUtil.createJwt((AuthDetails) authentication.getPrincipal()));
        }
        throw new InvalidCredentialsException("Invalid Credentials");
    }


}
