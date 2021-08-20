package com.ss.user.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
public class DocsController {

    @RequestMapping(value = "/docs", method = RequestMethod.GET)
    public String index() {
        return "redirect:swagger-ui.html";
    }

}
