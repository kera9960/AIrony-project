package com.example.aironyproject.domain.chat.dto.request;

import com.example.aironyproject.domain.chat.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(
    @NotBlank(message = "메세지 내용은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "메세지 내용은 1000자 이하로 입력해주세요.")
    String content
) {
}
