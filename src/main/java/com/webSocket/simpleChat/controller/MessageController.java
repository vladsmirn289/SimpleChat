package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String user1,
                                                     @PathVariable String user2) {
        logger.info("Getting messages of users " + user1 + " and " + user2);
        List<Message> messages = messageService.findMessagesOfTwoUsers(user1, user2);
        messages.sort(Comparator.comparing(Message::getCreatedOn).reversed());

        return ResponseEntity.ok(messages);
    }
}
