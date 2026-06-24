package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.time.LocalDateTime;
import java.util.List;

public record GetChatRoomDetailResponse(
    Long chatRoomId,
    Long accommodationId,
    Long adminId,
    String title,
    ChatRoomStatus status,
    LocalDateTime createdAt,
    List<GetChatMessageResponse> messages
) {
  public static GetChatRoomDetailResponse from(
      ChatRoom chatRoom,
      List<ChatMessage> messages
  ) {
    return new GetChatRoomDetailResponse(
        chatRoom.getId(),
        chatRoom.getAccommodation() == null ? null : chatRoom.getAccommodation().getId(),
        chatRoom.getAdmin() == null ? null : chatRoom.getAdmin().getId(),
        chatRoom.getTitle(),
        chatRoom.getStatus(),
        chatRoom.getCreatedAt(),
        messages.stream()
            .map(GetChatMessageResponse::from)
            .toList()
    );
  }
}
