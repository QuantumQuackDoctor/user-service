package com.ss.user.api;

import com.ss.user.errors.exceptions.InvalidCredentialsException;
import com.ss.user.util.jwt.JwtUtil;
import com.ss.user.model.AuthRequest;
import com.ss.user.model.AuthResponse;
import com.ss.user.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T14:52:34.175022700-06:00[America/Denver]")
@Controller
@RequestMapping("${openapi.orchestrator.base-path:}")
public class LoginApiController implements LoginApi {

    private final NativeWebRequest request;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public LoginApiController(NativeWebRequest request, JwtUtil jwtUtil,AuthService authService) {
        this.request = request;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
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
    @Override
    public ResponseEntity<AuthResponse> postLogin(AuthRequest authRequest) throws InvalidCredentialsException {
        if(authRequest.validate()){
            return ResponseEntity.ok(authService.authenticate(authRequest));
        }
        throw new InvalidCredentialsException("Missing field");
    }
}