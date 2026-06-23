package com.example.aironyproject.domain.reservation.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_number",
            unique = true,
            nullable = false,
            length = 50
    )
    private String reservationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "original_price", nullable = false, columnDefinition = "INT UNSIGNED")
    private int originalPrice;

    @Column(name = "discount_amount", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int discountAmount;

    @Column(name = "final_price", nullable = false, columnDefinition = "INT UNSIGNED")
    private int finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReservationStatus status;

    public Reservation(
            String reservationNumber,
            User user,
            Accommodation accommodation,
            UserCoupon userCoupon,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int originalPrice,
            int discountAmount
    ) {
        ValidCheckOutDate(checkInDate, checkOutDate);
        ValidCheckInDate(checkInDate, checkOutDate);

        this.reservationNumber = reservationNumber;
        this.user = user;
        this.accommodation = accommodation;
        this.userCoupon = userCoupon;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.originalPrice = originalPrice;
        this.discountAmount = discountAmount;
        this.finalPrice = Math.max(originalPrice - discountAmount, 0); // 기존금액-할인금액이 음수일 시 0으로 처리
        this.status = ReservationStatus.PENDING_PAYMENT;
    }

    // 결제 성공 시 예약 확정 처리
    public void confirm() {
        changeStatus(ReservationStatus.CONFIRMED);
    }

    // 결제 실패, 예약 취소 또는 환불 완료 시 예약 취소 처리
    public void cancel() {
        changeStatus(ReservationStatus.CANCELED);
    }


    // 예약상태 변경 메서드
    private void changeStatus(ReservationStatus target) {
        if (!status.canTransitTo(target)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        this.status = target;
    }

    LocalDate today = LocalDate.now();


    // 예약할 때 체크아웃 날짜가 체크인 날짜 이후인지
    private void ValidCheckOutDate(
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        if (checkInDate == null ||
                checkOutDate == null ||
                !checkInDate.isBefore(checkOutDate)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_DATE);
        }
    }

    // 체크인 날짜가 예약 당일 날짜 이전인지 검증
    private void ValidCheckInDate(
            LocalDate checkInDate,
            LocalDate today
    ) {
        if (checkInDate == null ||
                today == null ||
                checkInDate.isBefore(today)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_DATE);
        }
    }
}