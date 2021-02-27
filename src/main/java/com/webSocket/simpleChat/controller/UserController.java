package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> findUsers(@RequestParam String search) {
        logger.info("Searching users by keyword: " + search);
        List<User> users = userService.searchUsers(search);

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
}
