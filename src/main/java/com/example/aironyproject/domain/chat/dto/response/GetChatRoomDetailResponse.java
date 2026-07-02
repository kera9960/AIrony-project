package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 상세 조회 응답 DTO
 * 채팅방의 기본 정보만 반환
 */
public record GetChatRoomDetailResponse(
    Long chatRoomId,
    Long accommodationId,
    Long adminId,
    String title,
    ChatRoomStatus status,
    LocalDateTime createdAt
) {
  public static GetChatRoomDetailResponse from(
      ChatRoom chatRoom) {
    return new GetChatRoomDetailResponse(
        chatRoom.getId(),
        chatRoom.getAccommodation() == null ? null : chatRoom.getAccommodation().getId(),
        chatRoom.getAdmin() == null ? null : chatRoom.getAdmin().getId(),
        chatRoom.getTitle(),
        chatRoom.getStatus(),
        chatRoom.getCreatedAt()
    );
  }
}
