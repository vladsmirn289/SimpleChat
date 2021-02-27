package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.recipient = :user1 AND m.sender = :user2) OR " +
            "(m.sender = :user1 AND m.recipient = :user2)")
    List<Message> findMessagesOfTwoUsers(@Param("user1") String user1,
                                         @Param("user2") String user2);
}
