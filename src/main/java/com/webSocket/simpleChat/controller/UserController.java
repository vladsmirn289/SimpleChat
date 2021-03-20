package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import com.webSocket.simpleChat.service.UserService;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> VALID_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "gif", "png"));

    @Value("${uploadPath}")
    private String uploadPath;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
        Map<String, String> errors = validateUserInfo(login, email, newPassword, repeatPassword, avatarFile);
        User userToChange = userService.findById(authUser.getId()).get();

        if (!errors.containsKey("loginError")) {
            userToChange.setLogin(login);
        }

        if (!errors.containsKey("emailError")) {
            //TODO: email login with confirmation
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

            userInfo.setAvatar(filename);
        }

        userToChange.setUserInfo(userInfo);
        userToChange.setNotification(notification);

        userService.save(userToChange);

        return ResponseEntity.ok(errors);
    }

    private Map<String, String> validateUserInfo(String login, String email,
                                                 String newPassword, String repeatPassword,
                                                 MultipartFile avatar) {
        Map<String, String> errors = new HashMap<>();
        if (login == null || login.isEmpty()) {
            logger.warn("Login cannot be empty!");
            errors.put("loginError", "Login cannot be empty");
        }

        Pattern emailPattern = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)" +
                "|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)" +
                "+[a-zA-Z]{2,}))$", Pattern.CASE_INSENSITIVE);
        if (email != null && !email.isEmpty()) {
            Matcher matcher = emailPattern.matcher(email);
            if (!matcher.find()) {
                logger.warn("Email incorrect!");
                errors.put("emailError", "Email incorrect");
            }
        }

        if (newPassword != null && repeatPassword != null && !newPassword.equals(repeatPassword)) {
            logger.warn("Password mismatch!");
            errors.put("passwordError", "Password mismatch");
        }

        if (avatar != null) {
            String[] filenameParts = avatar.getOriginalFilename().split("\\.");
            if ( filenameParts.length < 2 || !VALID_EXTENSIONS.contains(filenameParts[1]) ) {
                logger.warn("Invalid extension of avatar!");
                errors.put("extensionError", "Invalid extension of avatar");
            }

            if (avatar.getSize()/1024.0 > 800) {
                logger.warn("Max size of avatar must be 800K!");
                errors.put("sizeError", "Max size of avatar must be 800K");
            }
        }

        return errors;
    }
}
