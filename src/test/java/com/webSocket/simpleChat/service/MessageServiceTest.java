package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import com.webSocket.simpleChat.repository.MessageRepo;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageServiceTest {
    private final MessageRepo messageRepo = mock(MessageRepo.class);
    private final MessageService messageService = new MessageServiceImpl(messageRepo);
    private Message msg1 = new Message("user1", "user2", "content1");
    private Message msg2 = new Message("user2", "user1", "content2");

    @Test
    public void shouldFindMessageById() {
        when(messageRepo.findById(1L))
                .thenReturn(Optional.of(msg1));

        Optional<Message> found = messageService.findById(1L);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(msg1);
    }

    @Test
    public void shouldFindUnreadMessages() {
        when(messageRepo.findByRecipientAndStatusIs("rec", MessageStatus.SENT))
                .thenReturn(Collections.singletonList(msg1));

        List<Message> unread = messageService.findUnreadMessages("rec");
        assertThat(unread.size()).isEqualTo(1);
        assertThat(unread.get(0).getSender()).isEqualTo("user2");
    }

    @Test
    public void shouldFindMessagesOfTwoUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        when(messageRepo.findMessagesOfTwoUsers("user1", "user2", pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(msg1, msg2)));

        List<Message> messages = messageService.findMessagesOfTwoUsers("user1", "user2", pageable).getContent();
        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages).contains(msg1, msg2);
    }
}
