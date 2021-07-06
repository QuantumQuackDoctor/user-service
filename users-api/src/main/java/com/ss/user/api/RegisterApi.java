/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.ss.user.api;

import com.ss.user.model.InlineObject;
import com.ss.user.model.InlineResponse409;
import com.ss.user.model.User;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
@Validated
@Api(value = "register", description = "the register API")
public interface RegisterApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PUT /register : Register
     * TODO Register new user, email validation will be sent
     *
     * @param user User to register (optional)
     * @return Account Created (status code 200)
     * or Missing field (status code 400)
     * or username or email invalid (status code 409)
     */
    @ApiOperation(value = "Register", nickname = "putRegister", notes = "TODO Register new user, email validation will be sent", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Created"),
            @ApiResponse(code = 400, message = "Missing field"),
            @ApiResponse(code = 409, message = "username or email invalid", response = InlineResponse409.class)})
    @PutMapping(
            value = "/register",
            produces = {"application/json"},
            consumes = {"application/json", "application/xml"}
    )
    default ResponseEntity<?> putRegister(@ApiParam(value = "User to register") @Valid @RequestBody(required = true) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    default ResponseEntity<Void> resetPassword(@ApiParam(value = "Email") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PATCH /register : Update Password
     * Send new password, needs key from reset password
     *
     * @param inlineObject (optional)
     * @return Password Reset (status code 200)
     */
    @ApiOperation(value = "Update Password", nickname = "updatePassword", notes = "Send new password, needs key from reset password", tags = {"auth",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password Reset")})
    @PatchMapping(
            value = "/register",
            consumes = {"application/json"}
    )
    default ResponseEntity<Void> updatePassword(@ApiParam(value = "") @Valid @RequestBody(required = false) InlineObject inlineObject) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
