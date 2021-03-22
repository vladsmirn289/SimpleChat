package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.Role;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import com.webSocket.simpleChat.repository.UserRepo;
import com.webSocket.simpleChat.util.MailSenderUtil;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderUtil mailSender;

    @Value("${host}")
    private String host;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, MailSenderUtil mailSender) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        logger.info("Finding user by login: " + login);
        return userRepo.findByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String search, Pageable pageable) {
        logger.info("Searching users by keyword: " + search);
        return userRepo.searchUsers(search, pageable);
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
    @Transactional(readOnly = true)
    public List<User> findFollowers(User user) {
        logger.info("Searching followers by user " + user.getLogin());
        User managedUser = findByLogin(user.getLogin()).orElseThrow(
                () -> new RuntimeException("User " + user.getLogin() + " not found")
        );
        Hibernate.initialize(managedUser.getFriendsOf());
        return new ArrayList<>(managedUser.getFriendsOf());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> findByConfirmationCode(String confirmationCode) {
        return userRepo.findByConfirmationCode(confirmationCode);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            logger.warn("User is null, saving is cancel");
            return;
        }

        setDefaultValues(user);

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

    private void setDefaultValues(User user) {
        if (user.getEmail() != null && user.getEmail().isEmpty()) {
            user.setEmail(null);
        }

        Notification notification = user.getNotification();
        if (notification == null) {
            notification = new Notification();
            user.setNotification(notification);
        }

        UserInfo userInfo = user.getUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfo();
            user.setUserInfo(userInfo);
        }
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
    public void sendCodeForSetNewEmail(User user, String email) {
        logger.info("Send confirm code for setting new email");
        String code = UUID.randomUUID().toString();
        user.setConfirmationCode(code);

        try {
            mailSender.sendActivationMessage(
                    email,
                    user.getLogin(),
                    "http://" + host + "/user/setNewEmail/" + email + "/" + code);
        } catch (Exception e) {
            logger.error(e.toString());
            return;
        }

        save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByLogin(username).orElseThrow(
                () -> new UsernameNotFoundException("User with login " + username + " doesn't exists")
        );
    }
}
