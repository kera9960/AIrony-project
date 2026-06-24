package com.example.aironyproject.domain.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationRequest(

        @NotNull(message = "숙소 ID는 필수입니다.")
        Long accommodationId,

        Long userCouponId,

        @NotNull(message = "체크인 날짜는 필수입니다")
        @FutureOrPresent(message = "체크인 날짜가 현재시간 이전일 수 없습니다.") // 현재인지 미래인지 검증하는 어노테이션
        LocalDate checkInDate,

        @NotNull(message = "체크아웃 날짜는 필수입니다")
        @Future(message = "체크아웃 날짜는 현재 시간 이후여야 합니다.") // 미래인지 검증하는 어노테이션
        LocalDate checkOutDate
) {
}
