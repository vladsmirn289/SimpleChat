package com.webSocket.simpleChat.repository;

import com.webSocket.simpleChat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    Optional<User> findByConfirmationCode(String confirmationCode);

    @Query("SELECT u FROM User u WHERE lower(u.login) LIKE lower(concat('%', :search, '%')) ORDER BY u.login")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
}
