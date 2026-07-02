package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.enums.MessageType;
import java.time.LocalDateTime;

/**
 * 메시지 한 건에 대한 응답 DTO
 * 커서 기반 메시지 목록 조회 시 각 메시지 항목으로 사용
 */
public record GetChatMessageResponse(
    Long messageId,
    Long senderId,
    String content,
    MessageType messageType,
    LocalDateTime readAt,
    LocalDateTime createdAt
) {
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
