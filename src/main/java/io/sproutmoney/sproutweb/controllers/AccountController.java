package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/28/17

import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.AccountService;
import io.sproutmoney.sproutweb.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;

    private Authentication authentication;

    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
    public ModelAndView showAccountDetailPage(ModelAndView modelAndView, @PathVariable int id) {
        // Check if the requested account belongs to the user
        authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(authentication.getName());
        logger.info("Found user " + currentUser.getEmail() + " in current session");

        // Set and show the account if it belongs to the current user
        Account account = accountService.findById(id);
        if (account == null) {
            logger.info("User tried to visit an account that doesn't exist (" + id + ")");
            modelAndView.setViewName("unauthorized_account");
            return modelAndView;
        }
        if (account.getUser() == currentUser) {
            modelAndView.setViewName("account_detail");
            modelAndView.addObject("account", account);
        } else {
            modelAndView.setViewName("unauthorized_account");
        }

        //TODO: Handle error redirection if the account doesn't exist (direct visit)
        return modelAndView;
    }

    @RequestMapping(value = "unauthorized_account", method = RequestMethod.GET)
    public ModelAndView showUnauthorizedAccountPage(ModelAndView modelAndView) {
        logger.info("A user tried to access an account they do not own");
        modelAndView.setViewName("unauthorized_account");
        return modelAndView;
    }
}
