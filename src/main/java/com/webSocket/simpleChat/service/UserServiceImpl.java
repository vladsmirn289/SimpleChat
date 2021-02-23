package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Role;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

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
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByLogin(username).orElseThrow(
                () -> new UsernameNotFoundException("User with login " + username + " doesn't exists")
        );
    }
}
