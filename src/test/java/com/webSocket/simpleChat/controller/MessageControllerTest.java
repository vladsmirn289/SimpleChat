package com.webSocket.simpleChat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class MessageControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Message msg1, msg2;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    public void init() {
        msg1 = new Message("user1", "user2", "content1");
        msg1.setCreatedOn(LocalDateTime.of(2021, 2, 27, 23, 2));

        msg2 = new Message("user2", "user1", "content2");
        msg2.setCreatedOn(LocalDateTime.of(2021, 2, 27, 23, 3));
    }

    @Test
    public void shouldGetMessages() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(messageService.findMessagesOfTwoUsers("user1", "user2", pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(msg1, msg2)));

        mockMvc.perform(get("/message/user1/user2?page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new PageImpl<>(Arrays.asList(msg2, msg1)))));
    }

    @Test
    public void shouldFindUnreadMessages() throws Exception {
        when(messageService.findUnreadMessages("rec"))
                .thenReturn(Collections.singletonList(msg1));

        mockMvc.perform(get("/message/findUnread/rec"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(msg1))));
    }
}
