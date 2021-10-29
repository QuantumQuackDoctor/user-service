package com.ss.user.api;

import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.ss.user.errors.*;
import com.ss.user.model.*;
import com.ss.user.service.AuthService;
import com.ss.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping(value = "/accounts")
@CrossOrigin(origins = "https://api.drscrumptious.com")
public class AuthApiController {

    private final UserService userService;
    private final AuthService authService;

    @org.springframework.beans.factory.annotation.Autowired
    public AuthApiController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * PUT /register : Register
     * Register new user, email validation will be sent
     *
     * @param user User to register (optional)
     * @return Account Created (status code 200)
     * or Missing field (status code 400)
     * or username or email invalid (status code 409)
     */
    @PreAuthorize("permitAll()")
    @PutMapping(value = "/register", produces = {"application/json"}, consumes = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account Created"),
            @ApiResponse(code = 400, message = "Missing field"),
            @ApiResponse(code = 409, message = "username or email invalid")})
    @ApiOperation(value = "Register", nickname = "putRegister", notes = "TODO Register new user, email validation will be sent", tags = {"auth",})
    public ResponseEntity<String> putRegister(@RequestParam(defaultValue = "false") boolean admin, @RequestBody(required = true) @Valid @ApiParam("User to register") User user) throws InvalidAdminEmailException {
        //check if phone or email exist
        if (userService.emailAvailable(user.getEmail())) {
            //insert user
            userService.insertUser(user, admin);
            return ResponseEntity.ok("Account created");
        } else {
            return new ResponseEntity<>("Email taken", HttpStatus.CONFLICT);
        }
    }

    @ExceptionHandler(InvalidAdminEmailException.class)
    public ResponseEntity<String> invalidEmailForAdmin(InvalidAdminEmailException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(MessageRejectedException.class)
    public ResponseEntity<String> emailFailedToSend(MessageRejectedException e) {
        log.warn("MessageRejectedException - " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * POST /login : Login
     * Log in, resend verification email if not activated
     *
     * @param authRequest Authentication request (optional)
     * @return Authenticated (status code 200)
     * or Invalid user (status code 400)
     * or Account not activated (status code 401)
     */
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/login", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Authenticated", response = AuthResponse.class),
            @ApiResponse(code = 400, message = "Invalid user"),
            @ApiResponse(code = 401, message = "Account not activated", response = String.class)})
    @ApiOperation(value = "Login", nickname = "postLogin", notes = "Log in, resend verification email if not activated", response = AuthResponse.class, tags = {"auth",})
    public ResponseEntity<AuthResponse> postLogin(@RequestBody(required = true) @Valid @ApiParam("Authentication request") AuthRequest authRequest) throws InvalidCredentialsException {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/activate/{token}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account activated"),
            @ApiResponse(code = 410, message = "Activation expired")
    })
    @ApiOperation(value = "activate", nickname = "Activate account", response = String.class)
    public ResponseEntity<Void> postActivate(@PathVariable UUID token) throws ConfirmationTokenExpiredException, UserNotFoundException {
        userService.activateAccount(token);
        return ResponseEntity.ok(null);
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<String> tokenExpired(ConfirmationTokenExpiredException e) {
        return new ResponseEntity<>("", HttpStatus.GONE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFound(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> badCredentials(InvalidCredentialsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * POST /register : Reset password
     * Sends email with password reset key, send patch request to same path to update password. Check users and drivers
     *
     * @return Email sent (status code 200)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Reset password", nickname = "resetPassword", notes = "Sends email with password reset key", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email sent"),
            @ApiResponse(code = 404, message = "Not Found")})
    @GetMapping(
            value = "/reset-password/{portalType}/{email}"
    )
    public ResponseEntity<Void> resetPassword(@ApiParam("email") @PathVariable("email") String email,
                                              @ApiParam("portalType") @PathVariable("portalType") PortalType type) throws UserNotFoundException {
        authService.sendPasswordResetEmail(email, type);
        return ResponseEntity.ok(null);
    }

    /**
     * PATCH /register : Update Password
     * Send new password, needs key from reset password
     *
     * @param passwordResetRequest (optional)
     * @return Password Reset (status code 200)
     */
    @ApiOperation(value = "Update Password", nickname = "updatePassword", notes = "Send new password, needs key from reset password", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password Reset")})
    @PostMapping(
            value = "/reset-password",
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> updatePassword(@ApiParam(value = "") @Valid @RequestBody(required = false) PasswordResetRequest passwordResetRequest) throws UserNotFoundException, ResourceExpiredException {
        authService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(null);
    }

    /**
     * Get /authenticated : test method for authentication
     * returns 200 if authenticated 401 if not
     */
    @ApiOperation(value = "Test Authentication", nickname = "authTest", notes = "returns 200 if authenticated", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "authentication good"),
            @ApiResponse(code = 401, message = "not authenticated")
    })
    @GetMapping(
            value = "/authenticated"
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> testAuth() {
        return ResponseEntity.ok(null);
    }
}
