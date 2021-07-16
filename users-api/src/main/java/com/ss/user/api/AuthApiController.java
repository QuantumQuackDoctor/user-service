package com.ss.user.api;

import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.model.PasswordResetRequest;
import com.ss.user.model.User;
import com.ss.user.service.AuthService;
import com.ss.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/accounts")
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
    @PutMapping(value = "/register", produces = {"application/json"}, consumes = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account Created"),
            @ApiResponse(code = 400, message = "Missing field"),
            @ApiResponse(code = 409, message = "username or email invalid")})
    @ApiOperation(value = "Register", nickname = "putRegister", notes = "TODO Register new user, email validation will be sent", tags = {"auth",})
    public ResponseEntity<String> putRegister(@RequestBody(required = true) @Valid @ApiParam("User to register") User user) {
        //check if phone or email exist
        if (userService.emailAvailable(user.getEmail())) {
            //insert user
            userService.insertUser(user);
            return ResponseEntity.ok("Account created");
        } else {
            return new ResponseEntity<String>("Email taken", HttpStatus.CONFLICT);
        }
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
    @PreAuthorize("permitAll")
    @PostMapping(value = "/login", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Authenticated", response = AuthResponse.class),
            @ApiResponse(code = 400, message = "Invalid user"),
            @ApiResponse(code = 401, message = "Account not activated", response = String.class)})
    @ApiOperation(value = "Login", nickname = "postLogin", notes = "Log in, resend verification email if not activated", response = AuthResponse.class, tags = {"auth",})
    public ResponseEntity<AuthResponse> postLogin(@RequestBody(required = true) @Valid @ApiParam("Authentication request") AuthRequest authRequest) throws InvalidCredentialsException {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> badCredentials(InvalidCredentialsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * POST /register : Reset password
     * Sends email with password reset key, send patch request to same path to update password. Check users and drivers
     *
     * @param body Email (optional)
     * @return Email sent (status code 200)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Reset password", nickname = "resetPassword", notes = "Sends email with password reset key, send patch request to same path to update password. Check users and drivers", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email sent"),
            @ApiResponse(code = 404, message = "Not Found")})
    @PostMapping(
            value = "/register",
            consumes = {"application/json", "application/xml"}
    )
    public ResponseEntity<Void> resetPassword(@ApiParam(value = "Email") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    @PatchMapping(
            value = "/register",
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> updatePassword(@ApiParam(value = "") @Valid @RequestBody(required = false) PasswordResetRequest passwordResetRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
