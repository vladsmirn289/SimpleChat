package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Optional<Message> findById(Long id);
    List<Message> findUnreadMessages(String recipient);
    Page<Message> findMessagesOfTwoUsers(String user1, String user2, Pageable pageable);

    Message save(Message message);
}
