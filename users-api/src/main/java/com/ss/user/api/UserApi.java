/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.ss.user.api;

import com.ss.user.model.User;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T11:08:01.330939100-06:00[America/Denver]")
@Validated
@Api(value = "user", description = "the user API")
public interface UserApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /user : Delete Account
     * delete account (uses JWT to find account)
     *
     * @return Account Deleted (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Delete Account", nickname = "deleteUser", notes = "delete account (uses JWT to find account)", authorizations = {
        
        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Account Deleted"),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found") })
    @DeleteMapping(
        value = "/user",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> deleteUser() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /user : Get Account Details
     * Retrieves user data using JWT (id and password will be null)
     *
     * @return OK (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Get Account Details", nickname = "getUser", notes = "Retrieves user data using JWT (id and password will be null)", response = User.class, authorizations = {
        
        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = User.class),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 404, message = "Not Found") })
    @GetMapping(
        value = "/user",
        produces = { "application/json", "application/xml" }
    )
    default ResponseEntity<User> getUser(@RequestHeader("id") Long id, @RequestHeader("role") String role) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"veteranStatus\" : true, \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"settings\" : { \"theme\" : \"light\", \"notifications\" : { \"text\" : true, \"email\" : true } }, \"password\" : \"password\", \"DOB\" : \"2021-02-10T00:00:00.000Z\", \"id\" : \"id\", \"email\" : \"email@example.com\", \"points\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
                    String exampleString = "<null> <id>aeiou</id> <email>email@example.com</email> <firstName>aeiou</firstName> <lastName>aeiou</lastName> <DOB>2021-02-10T00:00:00.000Z</DOB> <password>aeiou</password> <veteranStatus>true</veteranStatus> <points>123</points> </null>";
                    ApiUtil.setExampleResponse(request, "application/xml", exampleString);
                    break;
                }
            }
        });
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
    default ResponseEntity<Void> patchUser(@ApiParam(value = "New user data, non null properties will be updated"  )  @Valid @RequestBody(required = false) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
