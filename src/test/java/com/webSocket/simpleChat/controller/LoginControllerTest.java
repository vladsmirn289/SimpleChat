package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import com.webSocket.simpleChat.repository.UserRepo;
import com.webSocket.simpleChat.util.MailSenderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @MockBean
    private MailSenderUtil mailSender;

    private User testUser;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @BeforeEach
    public void init() {
        testUser = new User("testUser", passwordEncoder.encode("pass"));
        testUser.setNotification(new Notification());
        testUser.setUserInfo(new UserInfo());
        userRepo.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    public void loginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void accessDenied() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void shouldSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testUser")
                        .param("password", "pass"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void shouldErrorLogin() throws Exception {
        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "testUser")
                .param("password", "wrongPass"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}
