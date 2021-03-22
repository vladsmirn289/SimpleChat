package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByLogin(String login);
    Optional<User> findById(Long id);
    Optional<User> findByConfirmationCode(String confirmationCode);
    Page<User> searchUsers(String search, Pageable pageable);
    List<User> findFriends(User user);
    List<User> findFollowers(User user);

    void save(User user);

    void setFriendIfAbsent(String user, String friend);

    void sendCodeForSetNewEmail(User user, String email);
}
