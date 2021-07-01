package com.ss.user.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
@Controller
@RequestMapping("${openapi.orchestrator.base-path:}")
public class RegisterApiController implements RegisterApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public RegisterApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
