package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.enums.MessageType;
import java.time.LocalDateTime;

/**
 * 메시지 한 건에 대한 응답 DTO
 * 채팅방 상세 조회 시 메시지 목록의 각 항목으로 사용
 */
public record GetChatMessageResponse(
    Long messageId,
    Long senderId,
    String content,
    MessageType messageType,
    LocalDateTime readAt,
    LocalDateTime createdAt
) {

  /**
   * ChatMessage 엔티티를 응답 DTO로 변환
   */
  public static GetChatMessageResponse from(ChatMessage message) {
    return new GetChatMessageResponse(
        message.getId(),
        message.getSender().getId(),
        message.getContent(),
        message.getMessageType(),
        message.getReadAt(),
        message.getCreatedAt()
    );
  }
}
