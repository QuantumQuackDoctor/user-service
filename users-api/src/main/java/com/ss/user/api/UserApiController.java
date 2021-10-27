package com.ss.user.api;

import com.database.security.AuthDetails;
import com.ss.user.errors.InvalidCredentialsException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.service.UserService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping(value = "/accounts")
public class UserApiController {

    private final UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /user : Get Account Details
     * Retrieves user data using JWT (id and password will be null)
     *
     * @return OK (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Not Found (status code 404)
     */
    @GetMapping(value = "/user", produces = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = User.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Get Account Details", nickname = "getUser", notes = "Retrieves user data using JWT (id and password will be null)", response = User.class, authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})

    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUser(Authentication authentication) throws UserNotFoundException {
        UserDetails authDetails = (UserDetails) authentication.getPrincipal();
        User DTO = userService.getUser(authDetails.getUsername());
        return ResponseEntity.ok(DTO);
    }

    /**
     * DELETE /user : Delete Account (uses JWT to find account)
     *
     * @return Account Deleted (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @DeleteMapping(value = "/user", produces = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account Deleted"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Delete Account", nickname = "deleteUser", notes = "delete account (uses JWT to find account)", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})

    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Void> deleteUser(Authentication authentication) {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        userService.deleteUser(authDetails.getId());
        return ResponseEntity.ok(null);
    }

    /**
     * PATCH /user : Update Account Details
     * update user with JWT data
     *
     * @param user New user data, non null properties will be updated (optional)
     * @return Update Successful (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found, something weird happened. Recommended to reauthenticate (status code 404)
     */
    @ApiOperation(value = "Update Account Details", nickname = "patchUser", notes = "update user with JWT data", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update Successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found, something weird happened. Recommended to reauthenticate")})
    @PatchMapping(
            value = "/user",
            produces = {"application/json"},
            consumes = {"application/json", "application/xml"}
    )

    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<User> patchUser(@ApiParam(value = "New user data, non null properties will be updated") @Valid @RequestBody(required = false) User user,
                                          Authentication authentication) throws UserNotFoundException, InvalidCredentialsException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        if (Objects.equals(authDetails.getId(), user.getId())){
            return ResponseEntity.ok(userService.updateProfile(user));
        }
        throw new InvalidCredentialsException("Cannot update other user information.");
    }

}
