package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageServiceTest {
    private final MessageService messageService = mock(MessageService.class);
    private Message msg1 = new Message("user1", "user2", "content1");
    private Message msg2 = new Message("user2", "user1", "content2");

    @Test
    public void shouldFindMessageById() {
        when(messageService.findById(1L))
                .thenReturn(Optional.of(msg1));

        Optional<Message> found = messageService.findById(1L);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(msg1);
    }

    @Test
    public void shouldFindMessagesOfTwoUsers() {
        when(messageService.findMessagesOfTwoUsers("user1", "user2"))
                .thenReturn(Arrays.asList(msg1, msg2));

        List<Message> messages = messageService.findMessagesOfTwoUsers("user1", "user2");
        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages).contains(msg1, msg2);
    }
}
