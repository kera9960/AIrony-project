package com.example.aironyproject.domain.userCoupon.entity;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.coupon.entity.Coupon;
import com.example.aironyproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_coupon_user_coupon",
                columnNames = {"user_id", "coupon_id"}
                )
            }
        )
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    public UserCoupon(User user, Coupon coupon, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }

    // 쿠폰이 사용 가능한지 확인하고 할인 금액 계산
    public int calculateDiscount(int originalPrice) {
        LocalDateTime now = LocalDateTime.now();

        // 이미 사용한 쿠폰인지 검증
        if (usedAt != null) {
            throw new CustomException(ErrorCode.USER_COUPON_ALREADY_USED);
        }

        // 만료된 쿠폰인지 검증
        if (expiredAt != null && !now.isBefore(expiredAt)) {
            throw new CustomException(ErrorCode.USER_COUPON_EXPIRED);
        }

        // 할인 금액이 결제 금액보다 클 수 없도록 제한
        return Math.min(
                originalPrice,
                coupon.getDiscountAmount()
        );
    }

    // 쿠폰 사용 메서드
    public void use(LocalDateTime now) {
        if (usedAt != null) {
            throw new CustomException(ErrorCode.USER_COUPON_ALREADY_USED);
        }

        if (expiredAt != null && !now.isBefore(expiredAt)) {
            throw new CustomException(ErrorCode.USER_COUPON_EXPIRED);
        }

        this.usedAt = now;
    }
}
