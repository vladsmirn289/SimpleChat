package com.webSocket.simpleChat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webSocket.simpleChat.model.Notification;
import com.webSocket.simpleChat.model.Role;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.model.UserInfo;
import com.webSocket.simpleChat.service.UserService;
import com.webSocket.simpleChat.util.MailSenderUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Value("${uploadPath}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MailSenderUtil mailSender;

    @BeforeEach
    public void init() {
        user1 = new User("user1", "pass");
        user2 = new User("user2", "pass");
    }

    @Test
    public void shouldFindUserById() throws Exception {
        when(userService.findById(1L))
                .thenReturn(Optional.of(user1));

        mockMvc.perform(get("/user/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    public void shouldFindUsersBySearch() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(userService.searchUsers("se", pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(user1, user2)));

        mockMvc.perform(get("/user?search=se&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new PageImpl<>(Arrays.asList(user1, user2)))));
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
    public void shouldResendConfirmation() throws Exception {
        when(userService.findByLogin("user1"))
                .thenReturn(Optional.of(user1));

        mockMvc.perform(get("/user/resendConfirmation/user1/email@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("An email was sent, please check your inbox"));

        Mockito.verify(userService, times(1))
                .sendCodeForSetNewEmail(
                        eq(user1),
                        eq("email@gmail.com")
                );
    }

    @Test
    public void shouldChangeEmail() throws Exception {
        user1.setConfirmationCode("confirm-code");
        when(userService.findByConfirmationCode("confirm-code"))
                .thenReturn(Optional.of(user1));

        mockMvc.perform(get("/user/setNewEmail/email@gmail.com/confirm-code"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Email successfully changed"));

        assertThat(user1.getConfirmationCode()).isNull();
        assertThat(user1.getEmail()).isEqualTo("email@gmail.com");
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

    @Test
    public void shouldChangePersonalInfo() throws Exception {
        User authUser = new User("login", "pass");
        authUser.setId(1L);
        authUser.setRoles(Collections.singleton(Role.USER));
        authUser.setNotification(new Notification());
        authUser.setUserInfo(new UserInfo());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authUser, null,
                        Collections.singleton(Role.USER));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.findById(1L))
                .thenReturn(Optional.of(authUser));

        File testFile = new File("src/test/resources/images/user-male-circle.png");
        byte[] image = IOUtils.toByteArray(new FileInputStream(testFile));
        MockMultipartFile multipartFile = new MockMultipartFile(
                "avatarFile", "user-male-circle.jpg", "image/jpg", image);

        MockHttpServletRequestBuilder builder = multipart("/user/changePersonalInfo")
                .file(multipartFile)
                .with(csrf())
                .param("login", "Login")
                .param("email", "email@gmail.com")
                .param("realName", "Real name")
                .param("avatar", "user-male-circle.png")
                .param("bio", "My bio info")
                .param("birthday", "01.01.1900")
                .param("country", "Russian Federation")
                .param("phoneNumber", "+7 (914) 000-00-00")
                .param("emailOffline", "false")
                .param("newPassword", "newPass")
                .param("repeatPassword", "newPass")
                .principal(authentication);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new HashMap<>())));

        Files.delete(Paths.get(path + authUser.getUserInfo().getAvatar()));

        Mockito.verify(userService, times(1))
                .sendCodeForSetNewEmail(
                        eq(authUser),
                        eq("email@gmail.com")
                );

        assertThat(authUser.getLogin()).isEqualTo("Login");
        assertThat(authUser.getEmail()).isEqualTo("email@gmail.com");
        assertThat(authUser.getUserInfo().getRealName()).isEqualTo("Real name");
        assertThat(authUser.getUserInfo().getBio()).isEqualTo("My bio info");
        assertThat(authUser.getUserInfo().getBirthday()).isEqualTo("01.01.1900");
        assertThat(authUser.getUserInfo().getCountry()).isEqualTo("Russian Federation");
        assertThat(authUser.getUserInfo().getPhoneNumber()).isEqualTo("+7 (914) 000-00-00");
        assertThat(authUser.getNotification().isEmailOffline()).isFalse();
        assertThat(passwordEncoder.matches("newPass", authUser.getPassword())).isTrue();
    }

    @Test
    public void shouldChangePersonalInfoWithSomeErrors() throws Exception {
        User authUser = new User("login", "pass");
        authUser.setId(1L);
        authUser.setRoles(Collections.singleton(Role.USER));
        authUser.setNotification(new Notification());
        authUser.setUserInfo(new UserInfo());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authUser, null,
                        Collections.singleton(Role.USER));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.findById(1L))
                .thenReturn(Optional.of(authUser));

        MockHttpServletRequestBuilder builder = multipart("/user/changePersonalInfo")
                .with(csrf())
                .param("login", "")
                .param("email", "email")
                .param("realName", "Real name")
                .param("avatar", "user-male-circle.png")
                .param("bio", "My bio info")
                .param("birthday", "01.01.1900")
                .param("country", "Russian Federation")
                .param("phoneNumber", "+7 (914) 000-00-00")
                .param("emailOffline", "false")
                .param("newPassword", "1")
                .param("repeatPassword", "2")
                .principal(authentication);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"emailError\":\"Email incorrect\",\"passwordError\":\"Password mismatch\"," +
                                "\"loginError\":\"Login cannot be empty\"}"
                ));

        assertThat(authUser.getLogin()).isEqualTo("login");
        assertThat(authUser.getEmail()).isNull();
        assertThat(authUser.getUserInfo().getRealName()).isEqualTo("Real name");
        assertThat(authUser.getUserInfo().getBio()).isEqualTo("My bio info");
        assertThat(authUser.getUserInfo().getBirthday()).isEqualTo("01.01.1900");
        assertThat(authUser.getUserInfo().getCountry()).isEqualTo("Russian Federation");
        assertThat(authUser.getUserInfo().getPhoneNumber()).isEqualTo("+7 (914) 000-00-00");
        assertThat(authUser.getNotification().isEmailOffline()).isFalse();
        assertThat(authUser.getPassword()).isEqualTo("pass");
    }
}
