package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByLogin(String login);
    Optional<User> findById(Long id);
    List<User> searchUsers(String search);
    List<User> findFriends(User user);
    List<User> findFollowers(User user);

    void save(User user);

    void setFriendIfAbsent(String user, String friend);
}
