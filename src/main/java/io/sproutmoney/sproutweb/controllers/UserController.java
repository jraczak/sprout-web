package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/10/17

import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    protected String user(final Map<String, Object> model, final HttpServletRequest request) {
        logger.info("Viewing user page");

        String accessToken = (String) SessionUtils.get(request, "accessToken");
        String idToken = (String) SessionUtils.get(request, "idToken");

        if (accessToken != null) model.put("userId", accessToken);
        else if (idToken != null) model.put("userId", idToken);
        return "user";
    }
}
