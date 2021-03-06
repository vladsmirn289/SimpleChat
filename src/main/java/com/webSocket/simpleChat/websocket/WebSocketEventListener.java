package com.webSocket.simpleChat.websocket;

import com.webSocket.simpleChat.jackson.SimpleUserDTO;
import com.webSocket.simpleChat.model.User;
import com.webSocket.simpleChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @Autowired
    public WebSocketEventListener(SimpMessagingTemplate simpMessagingTemplate,
                                  UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userService = userService;
    }

    @EventListener
    public void handleWebSocketConnecting(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            String username = principal.getName();
            logger.info("User " + username + " connected to chat");

            setUserStatus(username, "online");
            return;
        }

        logger.warn("Session doesn't contains user");
    }

    @EventListener
    public void handleWebSocketDisconnecting(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            String username = principal.getName();
            logger.info("User " + username + " disconnected");

            setUserStatus(username, "offline");
            return;
        }

        logger.warn("Session doesn't contains user");
    }

    private void setUserStatus(String username,
                               String status) {
        logger.info("Setting user " + username + " " + status);
        User user = userService.findByLogin(username).orElseThrow(
                () -> new RuntimeException("User " + username + " not found")
        );

        if (status.equals("online")) {
            user.setOnline(true);
        } else if (status.equals("offline")) {
            user.setOnline(false);
        }
        userService.save(user);

        SimpleUserDTO userDTO = new SimpleUserDTO();
        List<User> followers = userService.findFollowers(user);
        for (User follower : followers) {
            BeanUtils.copyProperties(user, userDTO);
            simpMessagingTemplate.convertAndSend("/topic/userStatus/" + follower.getLogin(), userDTO);
        }
    }
}
