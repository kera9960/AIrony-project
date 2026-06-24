package com.example.aironyproject.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelReservationRequest(
        @NotBlank(message = "취소 및 환불 사유는 필수입니다.")
        @Size(max = 100, message = "취소 및 환불 사유는 100자 이하여야 합니다.")
        String reason
) {
}
