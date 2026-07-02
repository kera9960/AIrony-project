package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;

public record AcceptChatRoomResponse(
    Long chatRoomId,
    Long adminId,
    ChatRoomStatus status
) {
  public static AcceptChatRoomResponse from(ChatRoom chatRoom) {
    return new AcceptChatRoomResponse(
        chatRoom.getId(),
        chatRoom.getAdmin().getId(),
        chatRoom.getStatus()
    );
  }
}
