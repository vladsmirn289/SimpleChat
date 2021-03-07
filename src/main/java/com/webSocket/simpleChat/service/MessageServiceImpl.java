package com.webSocket.simpleChat.service;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import com.webSocket.simpleChat.repository.MessageRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<Message> findUnreadMessages(String recipient) {
        return messageRepo.findByRecipientAndStatusIs(recipient, MessageStatus.SENT);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> findMessagesOfTwoUsers(String user1, String user2, Pageable pageable) {
        logger.info("Searching messages of users " + user1 + " and " + user2);

        return messageRepo.findMessagesOfTwoUsers(user1, user2, pageable);
    }

    @Override
    public Message save(Message message) {
        logger.info("Saving message with recipient " + message.getRecipient() + " and sender " + message.getSender());
        return messageRepo.save(message);
    }
}
