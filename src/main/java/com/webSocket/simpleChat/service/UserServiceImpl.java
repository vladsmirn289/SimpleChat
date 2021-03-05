package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Role;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.repository.UserRepo;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        logger.info("Finding user by login: " + login);
        return userRepo.findByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String search) {
        logger.info("Searching users by keyword: " + search);
        return userRepo.searchUsers(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findFriends(User user) {
        logger.info("Searching all friends of user " + user.getLogin());
        User managedUser = findByLogin(user.getLogin()).orElseThrow(
                () -> new RuntimeException("User " + user.getLogin() + " not found")
        );
        Hibernate.initialize(managedUser.getUserFriends());
        return new ArrayList<>(managedUser.getUserFriends());
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            logger.warn("User is null, saving is cancel");
            return;
        }

        if (user.getEmail() != null && user.getEmail().isEmpty()) {
            user.setEmail(null);
        }

        if (user.getId() == null) {
            logger.info("Saving user with login " + user.getLogin());

            String plainPass = user.getPassword();
            String encodedPass = passwordEncoder.encode(plainPass);
            user.setPassword(encodedPass);
            user.setRoles(Collections.singleton(Role.USER));

            userRepo.save(user);

            logger.info("User with login " + user.getLogin() + " successfully saved");
            return;
        }

        logger.info("Updating user with login " + user.getLogin());
        userRepo.save(user);
        logger.info("User with login " + user.getLogin() + " successfully updated");
    }

    @Override
    public void setFriendIfAbsent(String user, String friend) {
        logger.info("Trying to set user " + friend + " to user " + user + " friends");
        User sourceUser = findByLogin(user)
                .orElseThrow(() -> new RuntimeException("User " + user + " not found"));
        User friendUser = findByLogin(friend)
                .orElseThrow(() -> new RuntimeException("User " + friend + " not found"));
        List<User> recipientFriends = findFriends(sourceUser);
        if (!recipientFriends.contains(friendUser)) {
            recipientFriends.add(friendUser);
            sourceUser.setUserFriends(new HashSet<>(recipientFriends));
            save(sourceUser);
            logger.info("User " + friend + " successfully set to user " + user + " friends");
            return;
        }

        logger.info("User " + friend + " already exists in user " + user + " friends");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByLogin(username).orElseThrow(
                () -> new UsernameNotFoundException("User with login " + username + " doesn't exists")
        );
    }
}
