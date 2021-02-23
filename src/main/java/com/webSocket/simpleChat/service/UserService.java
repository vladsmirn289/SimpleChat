package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByLogin(String login);
    void save(User user);
}
