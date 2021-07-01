package com.ss.user.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
public class DocsController {

    @RequestMapping("/docs")
    public String index() {
        return "redirect:swagger-ui.html";
    }

}
