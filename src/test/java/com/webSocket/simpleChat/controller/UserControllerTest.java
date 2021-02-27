package com.webSocket.simpleChat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user1, user2;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        user1 = new User("user1", "pass");
        user2 = new User("user2", "pass");
    }

    @Test
    public void shouldFindUsersBySearch() throws Exception {
        when(userService.searchUsers("se"))
                .thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user")
                        .param("search", "se"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user2))));
    }

    @Test
    public void shouldFindFriends() throws Exception {
        when(userService.findById(150L))
                .thenReturn(Optional.of(user1));

        when(userService.findFriends(user1))
                .thenReturn(Collections.singletonList(user2));

        mockMvc.perform(get("/user/friends/150"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(user2))));
    }

    @Test
    public void notFoundUserWhileSearchingFriends() throws Exception {
        mockMvc.perform(get("/user/friends/150"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddFriend() throws Exception {
        when(userService.findById(150L))
                .thenReturn(Optional.of(user1));

        when(userService.findById(200L))
                .thenReturn(Optional.of(user2));

        mockMvc.perform(post("/user/150/addFriend/200")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito
                .verify(userService, Mockito.times(1))
                .save(user1);

        assertThat(user1.getUserFriends()).contains(user2);
    }

    @Test
    public void notFoundWhileAddingFriend() throws Exception {
        mockMvc.perform(post("/user/150/addFriend/200")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
