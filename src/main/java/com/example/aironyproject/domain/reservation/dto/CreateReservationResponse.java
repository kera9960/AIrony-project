package com.example.aironyproject.domain.reservation.dto;

import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateReservationResponse(
        Long reservationId,
        String reservationNumber,
        ReservationStatus status,
        int originalPrice,
        int discountAmount,
        int finalPrice,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalDateTime createdAt
) {

    public static CreateReservationResponse from(Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                reservation.getOriginalPrice(),
                reservation.getDiscountAmount(),
                reservation.getFinalPrice(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getCreatedAt()
        );
    }



}
