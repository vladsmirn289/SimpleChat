package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MessageRepoTest {
    @Autowired
    private MessageRepo messageRepo;

    private Message msg1, msg2;

    @BeforeEach
    public void init() {
        msg1 = new Message("User1", "User2", "ContentOfMgs1");
        msg2 = new Message("User2", "User1", "ContentOfMsg2");
        msg1.setStatus(MessageStatus.SENT);
        msg2.setStatus(MessageStatus.READ);

        messageRepo.save(msg1);
        messageRepo.save(msg2);
    }

    @Test
    public void shouldFindByRecipientAndStatusIs() {
        List<Message> unread = messageRepo.findByRecipientAndStatusIs("User1", MessageStatus.SENT);
        assertThat(unread.size()).isEqualTo(1);
        assertThat(unread.get(0).getSender()).isEqualTo("User2");
    }

    @Test
    public void shouldFindByRecipientAndSender() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Message> messages = messageRepo.findMessagesOfTwoUsers("User1", "User2", pageable).getContent();

        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages).contains(msg1, msg2);
    }
}
