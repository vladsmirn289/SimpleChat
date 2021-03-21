package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final UserRepo userRepo = mock(UserRepo.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    private User testUser, testUser2;
    private final UserService userService = new UserServiceImpl(userRepo, passwordEncoder);

    @BeforeEach
    public void init() {
        testUser = new User("testLogin", "pass");
        testUser2= new User("testLogin2", "pass");
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
    void shouldFindById() {
        when(userRepo.findById(150L))
                .thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findById(150L);
        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(user.getPassword()).isEqualTo("pass");
    }

    @Test
    void shouldFindByConfirmCode() {
        when(userRepo.findByConfirmationCode("confirm-code"))
                .thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findByConfirmationCode("confirm-code");
        assertThat(found.isPresent()).isTrue();
        User user = found.get();
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(user.getPassword()).isEqualTo("pass");
    }

    @Test
    void shouldSearchUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        List<User> users = Arrays.asList(new User("user1", "pass"), new User("user2", "pass"));
        Page<User> page = new PageImpl<>(users);
        when(userRepo.searchUsers("se", pageable))
                .thenReturn(page);

        assertThat(userService.searchUsers("se", pageable).getContent())
                .isEqualTo(users);
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
    void shouldSetFriendIfAbsent() {
        when(userRepo.findByLogin("testLogin"))
                .thenReturn(Optional.of(testUser));

        when(userRepo.findByLogin("testLogin2"))
                .thenReturn(Optional.of(testUser2));

        userService.setFriendIfAbsent("testLogin", "testLogin2");
        assertThat(testUser.getUserFriends().size()).isEqualTo(1);
        assertThat(testUser.getUserFriends().iterator().next().getLogin()).isEqualTo("testLogin2");

        verify(userRepo, times(1))
                .save(testUser);
    }

    @Test
    void shouldNotSetUserWhenHeIsAlreadyFriend() {
        testUser.getUserFriends().add(testUser2);
        when(userRepo.findByLogin("testLogin"))
                .thenReturn(Optional.of(testUser));

        when(userRepo.findByLogin("testLogin2"))
                .thenReturn(Optional.of(testUser2));

        userService.setFriendIfAbsent("testLogin", "testLogin2");

        verify(userRepo, times(0))
                .save(testUser);
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
