package com.example.aironyproject.domain.coupon.dto;

import java.time.LocalDateTime;
import com.example.aironyproject.domain.coupon.entity.Coupon;


public record GetCouponResponse(
	String name,
	int discountAmount,
	LocalDateTime startedAt,
	LocalDateTime endedAt
) {
	public GetCouponResponse(Coupon coupon){
		this(coupon.getName(), coupon.getDiscountAmount(), coupon.getStartedAt(), coupon.getEndedAt());
	}
}
