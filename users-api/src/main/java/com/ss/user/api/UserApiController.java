package com.ss.user.api;

import com.ss.user.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T11:08:01.330939100-06:00[America/Denver]")
@Controller
@RequestMapping("${openapi.orchestrator.base-path:}")
public class UserApiController implements UserApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
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
    @Override
    public ResponseEntity<User> getUser(Long id, String role) {
        System.out.println(id);
        System.out.println(role);
        return ResponseEntity.ok(new User());
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
    @Override
    public ResponseEntity<Void> deleteUser() {
        return UserApi.super.deleteUser();
    }
}
