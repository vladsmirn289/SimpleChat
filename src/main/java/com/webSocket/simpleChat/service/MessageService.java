package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;

import java.util.List;

public interface MessageService {
    List<Message> findMessagesOfTwoUsers(String user1, String user2);
}
