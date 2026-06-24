package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 상세 조회 응답 DTO
 * 채팅방 정보와 해당 채팅방의 메시지 목록을 함께 반환
 */
public record GetChatRoomDetailResponse(
    Long chatRoomId,
    Long accommodationId,
    Long adminId,
    String title,
    ChatRoomStatus status,
    LocalDateTime createdAt,
    List<GetChatMessageResponse> messages
) {
  /**
   * ChatRoom 엔티티와 메시지 목록을 채팅방 상세 응답 DTO로 변환
   */
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
