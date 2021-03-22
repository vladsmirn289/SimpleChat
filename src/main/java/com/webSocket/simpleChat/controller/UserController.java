package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import com.webSocket.simpleChat.service.UserService;
import com.webSocket.simpleChat.util.MailSenderUtil;
import com.webSocket.simpleChat.util.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    //private final MailSenderUtil mailSender;

    @Value("${uploadPath}")
    private String uploadPath;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder){//}, MailSenderUtil mailSender) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        //this.mailSender = mailSender;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        logger.info("Searching user by id " + id);
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<User>> findUsers(@RequestParam String search,
                                                @PageableDefault(size = 9) Pageable pageable) {
        logger.info("Searching users by keyword: " + search);
        Page<User> users = userService.searchUsers(search, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/friends/{user_id}")
    public ResponseEntity<List<User>> findFriends(@PathVariable("user_id") Long userId) {
        logger.info("Searching friends of user with id = " + userId);
        User user = userService.findById(userId).orElse(null);

        if (user == null) {
            logger.error("User with id " + userId + " doesn't exists!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }

        List<User> users = userService.findFriends(user);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/resendConfirmation/{login}/{email}")
    public ResponseEntity<String> resendConfirmation(@PathVariable String login,
                                                     @PathVariable String email) {
        User user = userService.findByLogin(login).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with login " + login + " not found");
        }

        userService.sendCodeForSetNewEmail(user, email);
        return ResponseEntity.ok("An email was sent, please check your inbox");
    }

    @GetMapping("/setNewEmail/{email}/{confirmCode}")
    public ResponseEntity<String> changeEmail(@PathVariable String email,
                                            @PathVariable String confirmCode) {
        User user = userService.findByConfirmationCode(confirmCode).orElse(null);
        if (user == null) {
            logger.warn("User with confirm code " + confirmCode + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with confirm code " + confirmCode + " not found");
        }

        logger.info("Changing email for user " + user.getLogin());
        user.setConfirmationCode(null);
        user.setEmail(email);
        userService.save(user);

        return ResponseEntity.ok("Email successfully changed");
    }

    @PostMapping("/{user_id}/addFriend/{friend_id}")
    public ResponseEntity<Void> addFriend(@PathVariable("user_id") Long userId,
                                          @PathVariable("friend_id") Long friendId) {
        logger.info("Add new friend to user with id = " + userId);
        User user = userService.findById(userId).orElse(null);
        User friend = userService.findById(friendId).orElse(null);

        if (user == null || friend == null) {
            logger.error("User with id " + userId + " or friend with id " + friendId + " or both aren't exists!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        user.getUserFriends().add(friend);
        userService.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePersonalInfo")
    public ResponseEntity<Map<String, String>> changePersonalInfo(@AuthenticationPrincipal User authUser,
                                                     String login,
                                                     String email,
                                                     UserInfo userInfo,
                                                     Notification notification,
                                                     String newPassword,
                                                     String repeatPassword,
                                                     @RequestParam(required = false) MultipartFile avatarFile) throws IOException {
        logger.info("Changing personal info of user " + authUser.getLogin());
        Map<String, String> errors = ValidateUtil.validateUserInfo(login, email, newPassword, repeatPassword, avatarFile);
        User userToChange = userService.findById(authUser.getId()).get();

        if (!errors.containsKey("loginError")) {
            userToChange.setLogin(login);
        }

        if (email != null && !email.isEmpty() && !errors.containsKey("emailError") &&
                !email.equals(userToChange.getEmail())) {
            userToChange.setEmail(email);
            userService.sendCodeForSetNewEmail(userToChange, email);
        }

        if (newPassword != null && repeatPassword != null && !errors.containsKey("passwordError")) {
            userToChange.setPassword(passwordEncoder.encode(newPassword));
        }

        boolean hasAvatarError = errors.containsKey("extensionError") ||
                errors.containsKey("sizeError") ||
                avatarFile == null;
        if (!hasAvatarError) {
            String filename = UUID.randomUUID().toString() + "-" + avatarFile.getOriginalFilename();
            avatarFile.transferTo(new File(uploadPath + filename));

            String oldAvatar = userToChange.getUserInfo().getAvatar();
            if (!oldAvatar.startsWith("user-male")) {
                Files.delete(Paths.get(uploadPath + oldAvatar));
            }
            userInfo.setAvatar(filename);
        } else {
            userInfo.setAvatar(userToChange.getUserInfo().getAvatar());
        }

        userToChange.setUserInfo(userInfo);
        userToChange.setNotification(notification);

        userService.save(userToChange);

        return ResponseEntity.ok(errors);
    }
}
