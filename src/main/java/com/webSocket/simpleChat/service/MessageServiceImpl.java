package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.repository.MessageRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<Message> findById(Long id) {
        return messageRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findMessagesOfTwoUsers(String user1, String user2) {
        logger.info("Searching messages of users " + user1 + " and " + user2);

        return messageRepo.findMessagesOfTwoUsers(user1, user2);
    }

    @Override
    public Message save(Message message) {
        logger.info("Saving message with recipient " + message.getRecipient() + " and sender " + message.getSender());
        return messageRepo.save(message);
    }
}
