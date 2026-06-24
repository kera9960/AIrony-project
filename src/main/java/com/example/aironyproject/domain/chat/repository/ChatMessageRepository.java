package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  /**
   * 특정 문의방의 메시지 목록을 생성 시간 기준 오름차순으로 조회
   * 오래된 메시지부터 조회하여 채팅 화면에 순서대로 표시하기 위해 사용
   */
  List<ChatMessage> findAllByChatRoom_IdOrderByCreatedAtAsc(Long chatRoomId);
}
