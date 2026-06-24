package com.example.aironyproject.domain.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationRequest(

        @NotNull
        Long accommodationId,

        Long userCouponId,

        @NotNull
        @FutureOrPresent // 현재인지 미래인지 검증하는 어노테이션
        LocalDate checkInDate,

        @NotNull
        @Future // 미래인지 검증하는 어노테이션
        LocalDate checkOutDate
) {
}
