package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.Status;
import com.webSocket.simpleChat.repository.MessageRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepo messageRepo;

    @Autowired
    public MessageServiceImpl(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public List<Message> findMessagesOfTwoUsers(String user1, String user2) {
        logger.info("Searching messages of users " + user1 + " and " + user2);

        return messageRepo.findMessagesOfTwoUsers(user1, user2);
    }
}
