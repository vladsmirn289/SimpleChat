package com.webSocket.simpleChat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webSocket.simpleChat.jackson.LocalDateTimeSerializer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String recipient;
    private String sender;
    private String content;

    @Enumerated(value = EnumType.STRING)
    private MessageStatus status;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdOn;

    protected Message() {
    }

    public Message(String recipient, String sender, String content) {
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return recipient.equals(message.recipient) &&
                sender.equals(message.sender) &&
                content.equals(message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipient, sender, content);
    }
}
