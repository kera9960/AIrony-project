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
        LocalDate checkInDate,
        LocalDate checkOutDate,
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
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
