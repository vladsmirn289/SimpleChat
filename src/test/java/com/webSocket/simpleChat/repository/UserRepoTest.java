package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepoTest {
    @Autowired
    private UserRepo userRepo;

    private User user1;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @BeforeEach
    public void init() {
        user1 = new User("testLogin", passwordEncoder.encode("pass"));
        user1.setConfirmationCode("confirm-code");
        user1.setNotification(new Notification());
        user1.setUserInfo(new UserInfo());
        userRepo.save(user1);

        User user2 = new User("user2", "pass");
        user2.setUserInfo(new UserInfo());
        user2.setNotification(new Notification());
        userRepo.save(user2);

        User user3 = new User("user3", "pass");
        user3.setUserInfo(new UserInfo());
        user3.setNotification(new Notification());
        userRepo.save(user3);
    }

    @Test
    public void shouldFindUserByLogin() {
        Optional<User> found = userRepo.findByLogin("testLogin");

        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(passwordEncoder.matches("pass", user.getPassword())).isTrue();
    }

    @Test
    public void shouldFindUserByConfirmCode() {
        Optional<User> found = userRepo.findByConfirmationCode("confirm-code");

        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
    }

    @Test
    public void shouldSearchUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        List<User> users1 = userRepo.searchUsers("tlo", pageable).getContent();
        List<User> users2 = userRepo.searchUsers("test", pageable).getContent();
        List<User> users3 = userRepo.searchUsers("testLogin", pageable).getContent();

        assertThat(users1.size()).isEqualTo(1);
        assertThat(users2.size()).isEqualTo(1);
        assertThat(users3.size()).isEqualTo(1);
        assertThat(users1.get(0).getLogin())
                .isEqualTo(users2.get(0).getLogin())
                .isEqualTo(users3.get(0).getLogin())
                .isEqualTo("testLogin");

        List<User> users4 = userRepo.searchUsers("s", pageable).getContent();
        assertThat(users4.size()).isEqualTo(3);
    }
}
