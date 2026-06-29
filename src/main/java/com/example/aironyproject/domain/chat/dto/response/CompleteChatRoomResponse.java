package com.example.aironyproject.domain.chat.dto.response;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.time.LocalDateTime;

public record CompleteChatRoomResponse(
    Long chatRoomId,
    Long adminId,
    ChatRoomStatus status,
    // COMPLETED 이후 채팅방은 조회만 허용하는 비즈니스 정책
    // 완료 처리 시 갱신된 updatedAt을 문의 완료 시각으로 사용
    LocalDateTime updatedAt
) {
  public static CompleteChatRoomResponse from(ChatRoom chatRoom) {
    return new CompleteChatRoomResponse(
        chatRoom.getId(),
        chatRoom.getAdmin().getId(),
        chatRoom.getStatus(),
        chatRoom.getUpdatedAt()
    );
  }
}
