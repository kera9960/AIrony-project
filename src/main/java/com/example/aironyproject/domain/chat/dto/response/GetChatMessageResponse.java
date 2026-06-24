package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.enums.MessageType;
import java.time.LocalDateTime;

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
