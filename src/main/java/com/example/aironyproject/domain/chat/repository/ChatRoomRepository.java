package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
