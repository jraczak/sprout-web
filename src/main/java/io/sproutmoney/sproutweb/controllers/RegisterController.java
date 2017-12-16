package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/11/17

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.EmailService;
import io.sproutmoney.sproutweb.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

@Controller
public class RegisterController {

    Logger logger = LoggerFactory.getLogger(RegisterController.class.getSimpleName());

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;

    @Autowired
    public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    @RequestMapping(value = "/register_success", method = RequestMethod.GET)
    public ModelAndView showRegistrationSuccessPage(ModelAndView modelAndView) {
        modelAndView.setViewName("register_success");
        return modelAndView;
    }

    // Return the registration form template
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    // Process registration form input
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(ModelAndView modelAndView,
                                                @Valid User user,
                                                BindingResult bindingResult,
                                                HttpServletRequest servletRequest,
                                                RedirectAttributes redirectAttributes) {

        logger.debug("Trying to process registration form");

        // Look user up in the database by email
        User userExists = userService.findByEmail(user.getEmail());

        logger.info(logger.getName() + "Checking if user exists");

        if (userExists != null) {
            modelAndView.addObject("alreadyRegisteredMessage", "Looks like there's already a " +
                    "user registered with that email address.");
            modelAndView.setViewName("register"); // Set the template for the redirect
            bindingResult.reject("email");
            logger.debug("Found existing user with email address");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register"); // Set the template for the redirect
        } else {
            //  This is a new user so create them and send a confirmation email

            // Disable the user until they have confirmed their email
            user.setEnabled(false);

            // Generate random 36-character string token for the confirmation link
            user.setConfirmationToken(UUID.randomUUID().toString());

            // Save the user to the database
            userService.saveUser(user);

            // Prepare and send the confirmation email
            String appUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName();

            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setText("To confirm your email address, please click the link below:\n" +
                    appUrl + "/confirm?token=" + user.getConfirmationToken());
            registrationEmail.setFrom("justin@sproutmoney.io");
            registrationEmail.setReplyTo("justin@sproutmoney.io");

            logger.debug("Sending account confirmation email to " + user.getEmail());
            emailService.sendEmail(registrationEmail);

            modelAndView.setViewName("redirect:register_success");
            redirectAttributes.addFlashAttribute("successMessage", "Account successfully created! " +
                            "Check your email for your confirmation.");
            modelAndView.addObject("confirmationMessage", "A confirmation email has been sent to " +
                    user.getEmail() + ". Please click the confirmation link to activate your account.");
        }
        return modelAndView;
    }

    // Process the confirmation link
    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ModelAndView showConfirmationPage(ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.findByConfirmationToken(token);

        if (user == null) {
            modelAndView.addObject("invalidToken", "Hmm. This seems to be an invalid confirmation link. " +
                    "Email help@sproutmoney.io if you're having trouble.");
        } else {
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        modelAndView.setViewName("confirm");
        return modelAndView;
    }

    // Process confirmation link (POST)
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public ModelAndView processConfirmationForm(ModelAndView modelAndView, BindingResult bindingResult,
                                                @RequestParam Map requestParams, RedirectAttributes redirectAttributes) {
        modelAndView.setViewName("confirm");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(requestParams.get("password").toString());

        if (strength.getScore() < 3) {
            bindingResult.reject("password");

            redirectAttributes.addFlashAttribute("errorMessage", "Your password is too weak. Please choose a stronger one.");
            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            logger.info(requestParams.get("token").toString());
            return modelAndView;
        } else {
            // Find the user with the rest token
            User user = userService.findByConfirmationToken(requestParams.get("token").toString());

            // Set the new password
            user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password").toString()));

            // Enable the user
            user.setEnabled(true);

            // Set to redirect to login
            modelAndView.setViewName("login");

            // Save the user
            userService.saveUser(user);

            modelAndView.addObject("successMessage", "Your password has been set!");
            System.out.println("Passwords match: " + bCryptPasswordEncoder.matches(requestParams.get("password").toString(), user.getPassword()));
            return modelAndView;
        }
    }
}
