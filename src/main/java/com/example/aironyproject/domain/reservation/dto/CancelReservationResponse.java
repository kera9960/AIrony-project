package com.example.aironyproject.domain.reservation.dto;

import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.payment.enums.PaymentStatus;
import com.example.aironyproject.domain.refund.entity.Refund;
import com.example.aironyproject.domain.refund.enums.RefundStatus;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

public record CancelReservationResponse(
        Long reservationId,
        String reservationNumber,
        ReservationStatus reservationStatus,

        Long paymentId,
        PaymentStatus paymentStatus,

        Long refundId,
        int refundAmount,
        RefundStatus refundStatus,
        LocalDateTime refundedAt
) {
    public static CancelReservationResponse from(
            Reservation reservation,
            Payment payment,
            Refund refund
    ) {
        return new CancelReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                payment.getId(),
                payment.getStatus(),
                refund.getId(),
                refund.getRefundAmount(),
                refund.getStatus(),
                refund.getRefundedAt()
        );
    }
}
