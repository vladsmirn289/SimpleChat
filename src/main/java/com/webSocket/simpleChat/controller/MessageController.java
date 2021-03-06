package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import com.webSocket.simpleChat.service.MessageService;
import com.webSocket.simpleChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessageController(MessageService messageService,
                             UserService userService,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String user1,
                                                     @PathVariable String user2) {
        logger.info("Getting messages of users " + user1 + " and " + user2);
        List<Message> messages = messageService.findMessagesOfTwoUsers(user1, user2);
        messages.sort(Comparator.comparing(Message::getCreatedOn));

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/findUnread/{recipient}")
    public ResponseEntity<List<Message>> findUnreadMessages(@PathVariable String recipient) {
        logger.info("Searching unread messages for recipient " + recipient);
        List<Message> unreadMessages = messageService.findUnreadMessages(recipient);

        return ResponseEntity.ok(unreadMessages);
    }

    @MessageMapping("/message/{recipient}/{sender}")
    public void sendMessage(@DestinationVariable String recipient,
                            @DestinationVariable String sender,
                            Message message) {
        logger.info("Incoming message from " + message.getSender() + " to " + message.getRecipient());

        userService.setFriendIfAbsent(recipient, sender);

        message.setStatus(MessageStatus.SENT);
        message = messageService.save(message);

        simpMessagingTemplate.convertAndSend("/topic/messages/" + recipient, message);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + sender, message);
    }

    @MessageMapping("/message/read/{message_id}")
    public void readMessage(@DestinationVariable("message_id") Long messageId) {
        logger.info("Changing status of message " + messageId + " on READ");

        Message message = messageService.findById(messageId).orElseThrow(
                () -> new RuntimeException("Message with id " + messageId + " not found")
        );

        message.setStatus(MessageStatus.READ);
        messageService.save(message);
        simpMessagingTemplate.convertAndSend("/topic/update/" + message.getSender(), message.getRecipient());
    }
}
