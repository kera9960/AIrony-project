package com.example.aironyproject.domain.reservation.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.payment.enums.PaymentStatus;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.entity.ReservationStatus;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetDetailReservationResponse(
        Long reservationId,
        String reservationNumber,
        ReservationStatus status,

        AccommodationInfo accommodation,

        LocalDate checkInDate,
        LocalDate checkOutDate,

        CouponInfo coupon,
        PaymentInfo payment,

        LocalDateTime createdAt
) {

    public record AccommodationInfo(
            Long accommodationId,
            String name,
            String address
    ) {
    }
    public record CouponInfo(
            Long userCouponId,
            String couponName
    ) {
    }

    public record PaymentInfo(
            Long paymentId,
            String paymentNumber,
            int amount,
            PaymentStatus status,
            LocalDateTime paidAt
    ) {
    }
        public static GetDetailReservationResponse from(Reservation reservation, Payment payment) {

            Accommodation accommodation =  reservation.getAccommodation();
            UserCoupon userCoupon = reservation.getUserCoupon();

            // 쿠폰을 사용하지 않은 예약이면 null
            CouponInfo couponInfo = userCoupon == null
                    ? null
                    : new CouponInfo(
                            userCoupon.getId(),
                            userCoupon.getCoupon().getName()
                    );

            return new GetDetailReservationResponse(
                    reservation.getId(),
                    reservation.getReservationNumber(),
                    reservation.getStatus(),

                    new AccommodationInfo(
                            accommodation.getId(),
                            accommodation.getName(),
                            accommodation.getAddress()
                    ),

                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),

                    couponInfo,
                    new PaymentInfo(
                            payment.getId(),
                            payment.getPaymentNumber(),
                            payment.getAmount(),
                            payment.getStatus(),
                            payment.getPaidAt()
                    ),
                    reservation.getCreatedAt()
            );
        }
}

