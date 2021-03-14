package com.webSocket.simpleChat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webSocket.simpleChat.jackson.StringDTO;
import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.MessageService;
import com.webSocket.simpleChat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class MessageControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Message msg1, msg2;

    @LocalServerPort
    private int port;
    private String URL;

    private CompletableFuture<Message> messageFuture;
    private CompletableFuture<StringDTO> stringFuture;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    public void init() {
        messageFuture = new CompletableFuture<>();
        stringFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/ws";

        msg1 = new Message("user" +
                "" +
                "1", "user2", "content1");
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

    @Test
    @Sql(scripts = "classpath:/sql/user-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void shouldSendMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Message msg = new Message("recipient", "sender", "content");
        when(messageService.save(msg))
                .thenReturn(msg);

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession1 = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);
        StompSession stompSession2 = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        stompSession1.subscribe("/topic/messages/sender", new MessageStompFrameHandler());
        stompSession2.subscribe("/topic/messages/recipient", new MessageStompFrameHandler());
        stompSession1.send("/chat/message/recipient/sender", msg);

        Message message1 = messageFuture.get(3, TimeUnit.SECONDS);
        Message message2 = messageFuture.get(3, TimeUnit.SECONDS);

        assertThat(message1).isEqualTo(message2);
        assertThat(message1.getRecipient()).isEqualTo("recipient");
        assertThat(message1.getSender()).isEqualTo("sender");
        assertThat(message1.getContent()).isEqualTo("content");

        Message passedToSave = new Message("recipient", "sender", "content");
        passedToSave.setStatus(MessageStatus.SENT);
        Mockito.verify(messageService, times(1))
                .save(passedToSave);
    }

    @Test
    public void shouldReadMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Message msg = new Message("recipient", "sender", "content");
        when(messageService.findById(1L))
                .thenReturn(Optional.of(msg));

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/update/sender", new StringStompFrameHandler());
        stompSession.send("/chat/message/read/1", "");

        StringDTO recipient = stringFuture.get(3, TimeUnit.SECONDS);

        assertThat(recipient.getMessage()).isEqualTo("recipient");

        Message passedToSave = new Message("recipient", "sender", "content");
        passedToSave.setStatus(MessageStatus.READ);
        Mockito.verify(messageService, times(1))
                .save(passedToSave);
    }

    private class MessageStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            messageFuture.complete((Message) o);
        }
    }

    private class StringStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return StringDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            stringFuture.complete((StringDTO) o);
        }
    }
}
