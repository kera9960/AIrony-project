package com.example.aironyproject.domain.chat.messaging;

import com.example.aironyproject.domain.chat.dto.response.GetChatMessageResponse;

public record ChatMessageEvent(
    Long chatRoomId,
    GetChatMessageResponse message
) {
}
