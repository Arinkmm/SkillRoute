package com.skillroute.repository;

import com.skillroute.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatIdOrderByCreatedAtAsc(Long chatId);

    Optional<Message> findFirstByChatIdOrderByCreatedAtDesc(Long chatId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.sender.id <> :userId")
    void markAsReadInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);
}