package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u WHERE lower(u.login) LIKE lower(concat('%', :search, '%'))")
    List<User> searchUsers(@Param("search") String search);
}
