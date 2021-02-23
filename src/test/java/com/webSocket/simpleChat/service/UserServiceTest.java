package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private final UserRepo userRepo = mock(UserRepo.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    private User testUser;
    private UserService userService = new UserServiceImpl(userRepo, passwordEncoder);

    @BeforeEach
    public void init() {
        testUser = new User("testLogin", "pass");
    }

    @Test
    void shouldFindByLogin() {
        when(userRepo.findByLogin("testLogin"))
                .thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findByLogin("testLogin");
        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(user.getPassword()).isEqualTo("pass");
    }

    @Test
    void shouldSaveNewUser() {
        when(userRepo.save(testUser))
                .then((u) -> {
                    testUser.setId(150L);
                    return testUser;
                });

        userService.save(testUser);
        assertThat(passwordEncoder.matches("pass", testUser.getPassword())).isTrue();
        assertThat(testUser.getAuthorities().size()).isEqualTo(1);
        assertThat(testUser.getEmail()).isEqualTo(null);
        assertThat(testUser.getId()).isEqualTo(150L);
        assertThat(testUser.getLogin()).isEqualTo("testLogin");
    }

    @Test
    void shouldUpdateUser() {
        testUser.setId(150L);
        when(userRepo.save(testUser))
                .thenReturn(testUser);

        userService.save(testUser);
        assertThat(testUser.getPassword()).isEqualTo("pass");
        assertThat(testUser.getAuthorities().size()).isEqualTo(0);
        assertThat(testUser.getEmail()).isEqualTo(null);
        assertThat(testUser.getId()).isEqualTo(150L);
        assertThat(testUser.getLogin()).isEqualTo("testLogin");
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepo.findByLogin("testLogin"))
                .thenReturn(Optional.of(testUser));

        UserDetails loaded = userService.loadUserByUsername("testLogin");
        assertThat(loaded.getUsername()).isEqualTo("testLogin");
        assertThat(loaded.getPassword()).isEqualTo("pass");
    }

    @Test
    void shouldThrowExceptionWhenTryToLoadUserByInvalidUsername() {
        when(userRepo.findByLogin("test"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test"));
    }
}
