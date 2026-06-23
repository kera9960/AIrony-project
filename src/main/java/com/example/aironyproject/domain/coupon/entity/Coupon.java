package com.example.aironyproject.domain.coupon.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@NoArgsConstructor
@Getter
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "discount_amount", nullable = false, columnDefinition = "INT UNSIGNED")
    private int discountAmount;

    @Column(name = "total_quantity", nullable = false, columnDefinition = "INT UNSIGNED")
    private int totalQuantity;

    @Column(name = "remaining_quantity", nullable = false, columnDefinition = "INT UNSIGNED")
    private int remainingQuantity;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status;

    // 쿠폰 발급 가능 여부를 검증하고 잔여 수량을 한 개 차감
    public void issue(LocalDateTime now) {
        validateIssuable(now);
        remainingQuantity--;
    }

    private void validateIssuable(LocalDateTime now) {
        if (status != CouponStatus.ACTIVE) {
            throw new CustomException(ErrorCode.COUPON_INACTIVE);
        }

        // 발급 가능 기간: startedAt <= now < endedAt
        if (now.isBefore(startedAt) ||
                !now.isBefore(endedAt)) {
            throw new CustomException(ErrorCode.COUPON_EXPIRED);
        }

        if (remainingQuantity <= 0) {
            throw new CustomException(ErrorCode.COUPON_SOLD_OUT);
        }
    }
}


