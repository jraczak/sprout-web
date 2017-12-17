package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/10/17


import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    UserService userService;

    Authentication authentication;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView showUserDashboard(ModelAndView modelAndView) {
        modelAndView.setViewName("dashboard");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current session is user: " + authentication.getName());
        User user = userService.findByEmail(authentication.getName());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

}
