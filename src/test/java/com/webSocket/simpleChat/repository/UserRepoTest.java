package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        userRepo.save(user1);

        userRepo.save(new User("user2", "pass"));
        userRepo.save(new User("user3", "pass"));
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
    public void shouldSearchUsers() {
        List<User> users1 = userRepo.searchUsers("tlo");
        List<User> users2 = userRepo.searchUsers("test");
        List<User> users3 = userRepo.searchUsers("testLogin");

        assertThat(users1.size()).isEqualTo(1);
        assertThat(users2.size()).isEqualTo(1);
        assertThat(users3.size()).isEqualTo(1);
        assertThat(users1.get(0).getLogin())
                .isEqualTo(users2.get(0).getLogin())
                .isEqualTo(users3.get(0).getLogin())
                .isEqualTo("testLogin");

        List<User> users4 = userRepo.searchUsers("s");
        assertThat(users4.size()).isEqualTo(3);
    }
}
