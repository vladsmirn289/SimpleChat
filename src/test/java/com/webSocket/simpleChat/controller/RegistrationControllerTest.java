package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    private User testUser;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @BeforeEach
    public void init() {
        testUser = new User("testUser", passwordEncoder.encode("pass"));
        userRepo.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    public void shouldSuccessRegisterNewUser() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("login", "justLogin")
                        .param("password", "justPass"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login#login_tab"))
                .andExpect(model().attributeDoesNotExist("errorMessage"));

        Optional<User> registeredUser = userRepo.findByLogin("justLogin");
        assertThat(registeredUser.isPresent()).isTrue();
        User newUser = registeredUser.get();
        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getLogin()).isEqualTo("justLogin");
        assertThat(passwordEncoder.matches("justPass", newUser.getPassword())).isTrue();
        assertThat(newUser.getRoles().size()).isEqualTo(1);
        assertThat(newUser.getEmail()).isNull();
    }

    @Test
    public void shouldErrorUserExistsWhenTryToRegisterNewUser() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("login", "testUser")
                .param("password", "justPass"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("errorMessage", "User with login testUser" +
                        " already exists!"));
    }

    @Test
    public void shouldValidationErrorWhenTryToRegisterNewUser() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("login", "")
                .param("password", "")
                .param("email", "email"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("errorMessage", containsString("#Login cannot be empty")))
                .andExpect(model().attribute("errorMessage", containsString("#Email must be corrected")))
                .andExpect(model().attribute("errorMessage", containsString("#Password cannot be empty")));
    }
}
