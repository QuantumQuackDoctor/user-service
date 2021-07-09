package com.ss.user.api;

import com.ss.user.model.User;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T11:08:01.330939100-06:00[America/Denver]")
@Controller
public class UsersApiController {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(NativeWebRequest request) {
        this.request = request;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * DELETE /users : Admin Delete User
     * Delete user with id
     *
     * @param body userId (optional)
     * @return Deleted (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Delete User", nickname = "deleteUsers", notes = "Delete user with id", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Deleted"),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found") })
    @DeleteMapping(
        value = "/users",
        produces = { "application/json" },
        consumes = { "application/json", "application/xml" }
    )
    public ResponseEntity<Void> deleteUsers(@ApiParam(value = "userId") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /users : Admin Get Users
     * Get all users or search users
     *
     * @param id Overwrites all other properties (optional)
     * @param email Overwrites all properties except id (optional)
     * @param name fuzzy string match, returns all with above %80ish match (optional)
     * @param veteran filters all veterans (optional)
     * @param points filters &gt;&#x3D; points (optional)
     * @param role filters by role (optional)
     * @param page page to return (optional)
     * @param size number of items in page (optional)
     * @return OK (status code 200)
     *         or Bad Query parameter (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Get Users", nickname = "getUsers", notes = "Get all users or search users", response = User.class, responseContainer = "List", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = User.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Query parameter", response = String.class),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found") })
    @GetMapping(
        value = "/users",
        produces = { "application/json" }
    )
    public ResponseEntity<List<User>> getUsers(@ApiParam(value = "Overwrites all other properties") @Valid @RequestParam(value = "id", required = false) String id, @ApiParam(value = "Overwrites all properties except id") @Valid @RequestParam(value = "email", required = false) String email, @ApiParam(value = "fuzzy string match, returns all with above %80ish match") @Valid @RequestParam(value = "name", required = false) String name, @ApiParam(value = "filters all veterans") @Valid @RequestParam(value = "veteran", required = false) Boolean veteran, @ApiParam(value = "filters >= points") @Valid @RequestParam(value = "points", required = false) Integer points, @ApiParam(value = "filters by role") @Valid @RequestParam(value = "role", required = false) String role, @Min(0) @ApiParam(value = "page to return") @Valid @RequestParam(value = "page", required = false) Integer page, @Min(1) @ApiParam(value = "number of items in page") @Valid @RequestParam(value = "size", required = false) Integer size) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"veteranStatus\" : true, \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"settings\" : { \"theme\" : \"light\", \"notifications\" : { \"text\" : true, \"email\" : true } }, \"password\" : \"password\", \"DOB\" : \"2021-02-10T00:00:00.000Z\", \"id\" : \"id\", \"email\" : \"email@example.com\", \"points\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /users : Admin Add User
     * Add new user
     *
     * @param user  (optional)
     * @return OK (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Email already exists (status code 409)
     */
    @ApiOperation(value = "Admin Add User", nickname = "putUsers", notes = "Add new user", response = User.class, authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = User.class),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 409, message = "Email already exists") })
    @PutMapping(
        value = "/users",
        produces = { "application/json", "application/xml" },
        consumes = { "application/json", "application/xml" }
    )
    public ResponseEntity<User> putUsers(@ApiParam(value = "") @Valid @RequestBody(required = false) User user) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"veteranStatus\" : true, \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"settings\" : { \"theme\" : \"light\", \"notifications\" : { \"text\" : true, \"email\" : true } }, \"password\" : \"password\", \"DOB\" : \"2021-02-10T00:00:00.000Z\", \"id\" : \"id\", \"email\" : \"email@example.com\", \"points\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
                    String exampleString = "<User> <id>aeiou</id> <email>email@example.com</email> <firstName>aeiou</firstName> <lastName>aeiou</lastName> <DOB>2021-02-10T00:00:00.000Z</DOB> <password>aeiou</password> <veteranStatus>true</veteranStatus> <points>123</points> </User>";
                    ApiUtil.setExampleResponse(request, "application/xml", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /users : Admin Update User
     * Update user with id, any other non null properties will be updated
     *
     * @param user User to update (optional)
     * @return OK (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Update User", nickname = "updateUser", notes = "Update user with id, any other non null properties will be updated", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found") })
    @PatchMapping(
        value = "/users",
        produces = { "application/json" },
        consumes = { "application/json", "application/xml" }
    )
    public ResponseEntity<Void> updateUser(@ApiParam(value = "User to update") @Valid @RequestBody(required = false) User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
}
