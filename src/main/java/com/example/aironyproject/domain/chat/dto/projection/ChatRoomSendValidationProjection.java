package com.example.aironyproject.domain.chat.dto.projection;

import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;

/**
 * 채팅 메시지 전송 가능 여부를 검증하기 위한 조회 전용 DTO
 *
 * ChatRoom 엔티티 전체를 조회하지 않고, 메시지 전송 검증에 필요한 최소 필드만 조회하기 위해 사용
 */
public record ChatRoomSendValidationProjection(
    Long chatRoomId,
    Long memberId,
    Long adminId,
    ChatRoomStatus status
) {
}
