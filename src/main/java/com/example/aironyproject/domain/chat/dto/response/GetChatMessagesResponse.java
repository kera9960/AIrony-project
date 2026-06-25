package com.example.aironyproject.domain.chat.dto.response;

import java.util.List;

/**
 * 커서 기반 메시지 목록 조회 응답 DTO
 *
 * messages는 조회된 메시지 목록
 * nextCursor는 다음 요청에 사용할 기준 메시지 ID
 * hasNext는 이전 메시지가 더 있는지 여부
 */
public record GetChatMessagesResponse(
    List<GetChatMessageResponse> messages,
    Long nextCursor,
    boolean hasNext
) {
}
