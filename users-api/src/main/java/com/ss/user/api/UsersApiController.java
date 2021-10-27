package com.ss.user.api;

import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.service.UserService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/accounts")
public class UsersApiController {

    private final UserService userService;

    public UsersApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * DELETE /users : Admin Delete User
     * Delete user with id
     *
     * @param body userId (optional)
     * @return Deleted (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Delete User", nickname = "deleteUsers", notes = "Delete user with id", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @DeleteMapping(
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json", "application/xml"}
    )

    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteUsers(@ApiParam(value = "userId") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /users : Admin Get Users
     * Get all users or search users
     *
     * @param id      Overwrites all other properties (optional)
     * @param email   Overwrites all properties except id (optional)
     * @return OK (status code 200)
     * or Bad Query parameter (status code 400)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Get User", nickname = "getUsers", notes = "Get all users or search users", response = User.class, authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Query parameter", response = String.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @GetMapping(
            value = "/users",
            produces = {"application/json"}
    )

    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<User> getUsers(@ApiParam(value = "Overwrites all other properties")
                                               @Valid @RequestParam(value = "id", required = false) Long id,
                                               @ApiParam(value = "Overwrites all properties except id")
                                               @Valid @RequestParam(value = "email", required = false) String email
    ) throws UserNotFoundException {
        if(id != null)
            return ResponseEntity.ok(userService.getUser(id));
        else if(email != null)
            return ResponseEntity.ok(userService.getUser(email));
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * PUT /users : Admin Add User
     * Add new user
     *
     * @param user (optional)
     * @return OK (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Email already exists (status code 409)
     */
    @ApiOperation(value = "Admin Add User", nickname = "putUsers", notes = "Add new user", response = User.class, authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = User.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 409, message = "Email already exists")})
    @PutMapping(
            value = "/users",
            produces = {"application/json", "application/xml"},
            consumes = {"application/json", "application/xml"}
    )

    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<User> putUsers(@ApiParam(value = "") @Valid @RequestBody(required = false) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /users : Admin Update User
     * Update user with id, any other non null properties will be updated
     *
     * @param user User to update (optional)
     * @return OK (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Update User", nickname = "updateUser", notes = "Update user with id, any other non null properties will be updated", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @PatchMapping(
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json", "application/xml"}
    )

    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateUser(@ApiParam(value = "User to update") @Valid @RequestBody(required = false) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
