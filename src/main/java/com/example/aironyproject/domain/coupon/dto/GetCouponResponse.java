package com.example.aironyproject.domain.coupon.dto;

import java.time.LocalDateTime;
import com.example.aironyproject.domain.coupon.entity.Coupon;
import com.example.aironyproject.domain.coupon.enums.CouponStatus;

public record GetCouponResponse(
	Long id,
	String name,
	int discountAmount,
	LocalDateTime startedAt,
	LocalDateTime endedAt,
	CouponStatus status
) {
	public static GetCouponResponse from(Coupon coupon){
		return new GetCouponResponse(
			coupon.getId(), coupon.getName(), coupon.getDiscountAmount(), coupon.getStartedAt(), coupon.getEndedAt(), coupon.getStatus());
	}
}
