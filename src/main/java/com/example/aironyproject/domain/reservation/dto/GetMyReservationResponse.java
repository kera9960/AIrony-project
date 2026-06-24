package com.example.aironyproject.domain.reservation.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetMyReservationResponse(
        Long reservationId,
        String reservationNumber,
        Long accommodationId,
        String accommodationName,
        String accommodationAddress,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int finalPrice,
        ReservationStatus status,
        LocalDateTime createdAt
) {

    public static GetMyReservationResponse from(Reservation reservation) {
        Accommodation accommodation = reservation.getAccommodation();

        return new GetMyReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getAddress(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getFinalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
