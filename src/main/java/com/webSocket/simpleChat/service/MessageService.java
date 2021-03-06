package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Optional<Message> findById(Long id);
    List<Message> findUnreadMessages(String recipient);
    List<Message> findMessagesOfTwoUsers(String user1, String user2);

    Message save(Message message);
}
