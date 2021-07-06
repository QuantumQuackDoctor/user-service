package com.ss.user.api;

import com.ss.user.model.User;
import com.ss.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
@Controller
public class RegisterApiController implements RegisterApi {

    private final NativeWebRequest request;
    private final UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public RegisterApiController(NativeWebRequest request, UserService userService) {
        this.request = request;
        this.userService = userService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
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
    private final static String[] nullableParams = {"id", "points", "phone"};

    @Override
    public ResponseEntity<?> putRegister(User user) {
        //check all fields populated
        List<String> nullFields = user.getNullFields();
        List<String> nullableFields = Arrays.asList(nullableParams);
        //remove nullable fields
        nullFields = nullFields.stream().filter(it -> !nullableFields.contains(it)).collect(Collectors.toList());
        if (nullFields.size() > 0) {
            return ResponseEntity.badRequest().body(nullFields);
        }
        //check if phone or email exist
        if (userService.emailValid(user.getEmail())) {
            //insert user
            userService.insertUser(user);
            return ResponseEntity.ok("Account created");
        } else {
            return new ResponseEntity<String>("Email taken", HttpStatus.CONFLICT);
        }

    }

}
