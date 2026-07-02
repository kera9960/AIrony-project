package com.example.aironyproject.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatRoomRequest(
    Long accommodationId,

    @NotBlank(message = "문의 제목은 필수입니다.")
    @Size(max = 100, message = "문의 제목은 100자 이하여야 합니다.")
    String title
) {
}
