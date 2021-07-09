package com.ss.user.api;

import com.ss.user.model.User;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T11:08:01.330939100-06:00[America/Denver]")
@Controller
public class UserApiController {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(NativeWebRequest request) {
        this.request = request;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * GET /user : Get Account Details
     * Retrieves user data using JWT (id and password will be null)
     *
     * @param id
     * @param role
     * @return OK (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Not Found (status code 404)
     */
    @org.springframework.web.bind.annotation.GetMapping(value = "/user", produces = {"application/json", "application/xml"})
    @io.swagger.annotations.ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = User.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @io.swagger.annotations.ApiOperation(value = "Get Account Details", nickname = "getUser", notes = "Retrieves user data using JWT (id and password will be null)", response = User.class, authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    public ResponseEntity<User> getUser() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /user : Delete Account
     * delete account (uses JWT to find account)
     *
     * @return Account Deleted (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @org.springframework.web.bind.annotation.DeleteMapping(value = "/user", produces = {"application/json"})
    @io.swagger.annotations.ApiResponses({
            @ApiResponse(code = 200, message = "Account Deleted"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @io.swagger.annotations.ApiOperation(value = "Delete Account", nickname = "deleteUser", notes = "delete account (uses JWT to find account)", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"user",})
    public ResponseEntity<Void> deleteUser() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PATCH /user : Update Account Details
     * update user with JWT data
     *
     * @param user New user data, non null properties will be updated (optional)
     * @return Update Successful (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found, something weird happened. Recommended to reauthenticate (status code 404)
     */
    @ApiOperation(value = "Update Account Details", nickname = "patchUser", notes = "update user with JWT data", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Update Successful"),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found, something weird happened. Recommended to reauthenticate") })
    @PatchMapping(
        value = "/user",
        produces = { "application/json" },
        consumes = { "application/json", "application/xml" }
    )
    public ResponseEntity<Void> patchUser(@ApiParam(value = "New user data, non null properties will be updated") @Valid @RequestBody(required = false) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
}
