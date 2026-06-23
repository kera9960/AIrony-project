package com.example.aironyproject.domain.userCoupon.entity;

import com.example.aironyproject.domain.coupon.entity.Coupon;
import com.example.aironyproject.domain.user.entity.User;
import jakarta.persistence.*;
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
@NoArgsConstructor
@Getter
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id")
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
}
