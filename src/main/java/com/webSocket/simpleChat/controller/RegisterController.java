package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute(name = "newUser") User newUser,
                               BindingResult bindingResult,
                               Model model) {
        logger.info("Trying to register new user...");
        boolean clientExists = userService.findByLogin(newUser.getLogin()).isPresent();

        if (clientExists) {
            String message = "User with login " + newUser.getLogin() + " already exists!";
            logger.warn(message);
            model.addAttribute("errorMessage", message);
            return "login";
        }

        if (bindingResult.hasErrors()) {
            logger.warn("Cannot register user, fields has errors!");
            String message = bindingResult.getFieldErrors()
                    .stream()
                    .peek(f -> logger.info(f.getField() + " has error: " + f.getDefaultMessage()))
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n#", "#", ""));

            model.addAttribute("errorMessage", message);
            return "login";
        }

        logger.info("Saving new user to database");
        userService.save(newUser);
        String email = newUser.getEmail();
        if (email != null && !email.isEmpty()) {
            userService.sendCodeForSetNewEmail(newUser, email);
        }
        logger.info("New user with login " + newUser.getLogin() + " successfully registered!");

        return "redirect:/login#login_tab";
    }
}
