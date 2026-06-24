package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
