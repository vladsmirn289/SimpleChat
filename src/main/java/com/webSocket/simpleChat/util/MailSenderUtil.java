package com.webSocket.simpleChat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailSenderUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailSenderUtil.class);

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public MailSenderUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(String to, String subject, String text) {
        logger.info("Sending email to " + to + " with subject " + subject);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    public void sendActivationMessage(String to, String name, String uri) {
        sendMessage(
                to,
                "Активация электронной почты",
                "Здравствуйте, " + name + "\nПожалуйста пройдите по данной ссылке, " +
                        "чтобы активировать почту:\n" + uri);
    }
}
