package com.example.aironyproject.domain.coupon.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.coupon.dto.GetCouponResponse;
import com.example.aironyproject.domain.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@GetMapping
	public ResponseEntity<CommonApiResponse<List<GetCouponResponse>>> getCoupons(){
		List<GetCouponResponse> data = couponService.getCoupon();

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "쿠폰 목록 조회 성공", data));
	}

	@PostMapping("/{couponId}/issue")
	public ResponseEntity<CommonApiResponse<GetCouponResponse>> issueCoupon(@PathVariable Long couponId,
		@AuthenticationPrincipal Long userId){
		GetCouponResponse data = couponService.registCoupon(userId, couponId);

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "쿠폰 발급 성공", data));
	}
}
