package com.ss.user.service;

import com.database.ormlibrary.user.UserEntity;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.util.jwt.JwtUtil;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest req) throws InvalidCredentialsException {
        Optional<UserEntity> entity = userRepo.findByEmail(req.getEmail());
        if (entity.isPresent()) {
            UserEntity userData = entity.get();
            //TODO FDA-15 add account activated check
            if (passwordEncoder.matches(req.getPassword(), userData.getPassword())) {
                //create jwt
                return new AuthResponse(jwtUtil.createJwt(userData));
            }
        }
        throw new InvalidCredentialsException("Invalid Credentials");
    }


}
