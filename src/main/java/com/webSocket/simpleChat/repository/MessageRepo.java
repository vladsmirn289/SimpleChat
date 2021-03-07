package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.Message;
import com.webSocket.simpleChat.model.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByRecipientAndStatusIs(String recipient, MessageStatus status);

    @Query("SELECT m FROM Message m WHERE (m.recipient = :user1 AND m.sender = :user2) OR " +
            "(m.sender = :user1 AND m.recipient = :user2) ORDER BY m.createdOn DESC")
    Page<Message> findMessagesOfTwoUsers(@Param("user1") String user1,
                                         @Param("user2") String user2,
                                         Pageable pageable);
}
