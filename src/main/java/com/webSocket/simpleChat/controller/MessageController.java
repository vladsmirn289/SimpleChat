package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.jackson.StringDTO;
import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.MessageService;
import com.webSocket.simpleChat.service.UserService;
import com.webSocket.simpleChat.util.MailSenderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MailSenderUtil mailSender;
    private final ExecutorService executorService;

    @Autowired
    public MessageController(MessageService messageService,
                             UserService userService,
                             SimpMessagingTemplate simpMessagingTemplate,
                             MailSenderUtil mailSender) {
        this.messageService = messageService;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mailSender = mailSender;

        executorService = Executors.newFixedThreadPool(50);
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<Page<Message>> getMessages(@PathVariable String user1,
                                                     @PathVariable String user2,
                                                     @PageableDefault(size = 15) Pageable pageable) {
        logger.info("Getting messages of users " + user1 + " and " + user2);
        Page<Message> messages = messageService.findMessagesOfTwoUsers(user1, user2, pageable);
        
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/findUnread/{recipient}")
    public ResponseEntity<List<Message>> findUnreadMessages(@PathVariable String recipient) {
        logger.info("Searching unread messages for recipient " + recipient);
        List<Message> unreadMessages = messageService.findUnreadMessages(recipient);

        return ResponseEntity.ok(unreadMessages);
    }

    @MessageMapping("/message/{recipient}/{sender}")
    public void sendMessageWithFriendship(@DestinationVariable String recipient,
                            @DestinationVariable String sender,
                            Message message) {
        logger.info("Incoming message from " + message.getSender() + " to " + message.getRecipient());

        userService.setFriendIfAbsent(recipient, sender);

        message.setStatus(MessageStatus.SENT);
        message = messageService.save(message);

        simpMessagingTemplate.convertAndSend("/topic/messages/" + recipient, message);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + sender, message);

        String messageContent = message.getContent();
        Runnable sendNotificationTask = () -> sendEmailNotification(recipient, sender, messageContent);
        executorService.submit(sendNotificationTask);
    }

    @MessageMapping("/message/read/{message_id}")
    public void readMessage(@DestinationVariable("message_id") Long messageId) {
        logger.info("Changing status of message " + messageId + " on READ");

        Message message = messageService.findById(messageId).orElseThrow(
                () -> new RuntimeException("Message with id " + messageId + " not found")
        );

        message.setStatus(MessageStatus.READ);
        messageService.save(message);
        StringDTO stringDTO = new StringDTO(message.getRecipient());
        simpMessagingTemplate.convertAndSend("/topic/update/" + message.getSender(), stringDTO);
    }

    private void sendEmailNotification(String recipientLogin, String senderLogin, String content) {
        User recipient = userService.findByLogin(recipientLogin).orElse(null);
        if (recipient == null) {
            return;
        }

        if (recipient.isOnline()) {
            return;
        }

        boolean isEmailOffline = recipient.getNotification().isEmailOffline();
        boolean confirmationCodeIsNull = recipient.getConfirmationCode() == null;
        boolean emailIsNotNullAndNotEmpty = recipient.getEmail() != null && !recipient.getEmail().isEmpty();
        if (isEmailOffline && confirmationCodeIsNull && emailIsNotNullAndNotEmpty) {
            mailSender.sendMessage(
                    recipient.getEmail(),
                    "New message from " + senderLogin,
                    "You have a new message from " + senderLogin + ": " + content
            );
        }
    }
}
