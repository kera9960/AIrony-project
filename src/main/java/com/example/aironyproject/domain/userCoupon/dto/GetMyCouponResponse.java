package com.example.aironyproject.domain.userCoupon.dto;

import java.time.LocalDateTime;

import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;

public record GetMyCouponResponse(
	Long couponId,
	String couponName,
	int discountAmount,
	LocalDateTime issuedAt,
	LocalDateTime expiredAt
) {
	public GetMyCouponResponse(UserCoupon coupon){
		this(coupon.getId(),
			 coupon.getCoupon().getName(),
			 coupon.getCoupon().getDiscountAmount(),
			 coupon.getIssuedAt(),
			 coupon.getExpiredAt());
	}
}
