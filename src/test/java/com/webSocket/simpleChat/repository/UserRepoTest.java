package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    }

    @Test
    public void shouldFindUserByLogin() {
        Optional<User> found = userRepo.findByLogin("testLogin");

        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(passwordEncoder.matches("pass", user.getPassword())).isTrue();
    }
}
