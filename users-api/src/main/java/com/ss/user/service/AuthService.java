package com.ss.user.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.database.ormlibrary.user.PasswordResetEntity;
import com.database.ormlibrary.user.UserEntity;
import com.database.security.AuthDetails;
import com.database.security.JwtTokenUtil;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.errors.ResourceExpiredException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.model.PasswordResetRequest;
import com.ss.user.model.PortalType;
import com.ss.user.repo.PasswordResetRepo;
import com.ss.user.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;
    private final PasswordResetRepo passwordResetRepo;
    private final AmazonSimpleEmailService ses;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${email.templates.password-reset}")
    private String passwordResetTemplate;
    @Value("${email.sender}")
    private String emailSender;
    @Value("${admin-portal-url}")
    private String adminPortalUrl;
    @Value("${user-portal-url}")
    private String userPortalUrl;
    @Value("${driver-portal-url}")
    private String driverPortalUrl;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenUtil jwtUtil,
                       PasswordResetRepo passwordResetRepo,
                       AmazonSimpleEmailService ses,
                       UserRepo userRepo,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordResetRepo = passwordResetRepo;
        this.ses = ses;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
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
        log.info("Authentication Failed - username: " + req.getEmail());
        throw new InvalidCredentialsException("Invalid Credentials");
    }


    @Transactional
    public void sendPasswordResetEmail(String email, PortalType type) throws UserNotFoundException {
        UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("email not found"));
        passwordResetRepo.deleteByUser(user);

        PasswordResetEntity resetRequest = createPasswordResetEntity(user);
        passwordResetRepo.save(resetRequest);

        String url;
        switch (type) {
            case user:
                url = userPortalUrl;
                break;
            case admin:
                url = adminPortalUrl;
                break;
            default:
                url = driverPortalUrl;
        }

        SendTemplatedEmailRequest sendTemplatedEmailRequest = new SendTemplatedEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withTemplate(passwordResetTemplate)
                .withTemplateData(String.format("{\"URL\": \"%s/reset/%s\", \"name\": \"%s\"}", url, resetRequest.getToken(), user.getFirstName()))
                .withSource(emailSender);

        ses.sendTemplatedEmail(sendTemplatedEmailRequest);
    }

    private PasswordResetEntity createPasswordResetEntity(UserEntity user) {
        PasswordResetEntity resetRequest = new PasswordResetEntity();
        resetRequest.setUser(user);
        resetRequest.setToken(UUID.randomUUID());
        resetRequest.setTokenExpiration(Instant.now().plusSeconds(45000));
        return resetRequest;
    }


    @Transactional
    public void resetPassword(PasswordResetRequest request) throws UserNotFoundException, ResourceExpiredException {
        PasswordResetEntity resetEntity = passwordResetRepo.findByToken(request.getToken())
                .orElseThrow(() -> new UserNotFoundException("token not found"));

        if (Instant.now().isAfter(resetEntity.getTokenExpiration()))
            throw new ResourceExpiredException("token no longer valid");

        UserEntity user = resetEntity.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        passwordResetRepo.delete(resetEntity);
    }

}
