package io.sproutmoney.sproutweb.auth;

//  Created by Justin on 12/10/17

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private AuthController controller;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    protected String login(final HttpServletRequest request) {
        logger.debug("Performing login");

        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/callback";
        String authorizeUrl = controller.buildAuthorizeUrl(request, redirectUri);
        return "redirect:" + authorizeUrl;
    }
}
