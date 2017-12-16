package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/10/17

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApplicationController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLogin(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView showTestPage(ModelAndView modelAndView) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            modelAndView.addObject("username", ((UserDetails)principal).getUsername());
        } else {
            modelAndView.addObject("username", principal.toString());
        }

        modelAndView.setViewName("test");
        return modelAndView;
    }

}
