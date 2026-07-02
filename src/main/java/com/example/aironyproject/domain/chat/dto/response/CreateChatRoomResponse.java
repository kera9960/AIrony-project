package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.time.LocalDateTime;

public record CreateChatRoomResponse(
    Long chatRoomId,
    Long accommodationId,
    String title,
    ChatRoomStatus status,
    LocalDateTime createdAt
) {

  public static CreateChatRoomResponse from(ChatRoom chatRoom) {
    return new CreateChatRoomResponse(
        chatRoom.getId(),
        chatRoom.getAccommodation() == null ? null : chatRoom.getAccommodation().getId(),
        chatRoom.getTitle(),
        chatRoom.getStatus(),
        chatRoom.getCreatedAt()
    );
  }
}
