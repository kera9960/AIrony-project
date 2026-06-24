package com.example.aironyproject.domain.reservation.dto;

import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.payment.enums.PaymentStatus;
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
        LocalDateTime createdAt,
        PaymentInfo paymentInfo
) {

    public record PaymentInfo(
            Long paymentId,
            String paymentNumber,
            int amount,
            PaymentStatus status,
            LocalDateTime paidAt
    ) {
    }

    public static CreateReservationResponse from(Reservation reservation, Payment payment) {
        return new CreateReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                reservation.getOriginalPrice(),
                reservation.getDiscountAmount(),
                reservation.getFinalPrice(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getCreatedAt(),
                new PaymentInfo(
                        payment.getId(),
                        payment.getPaymentNumber(),
                        payment.getAmount(),
                        payment.getStatus(),
                        payment.getPaidAt()
                )
        );
    }
}
