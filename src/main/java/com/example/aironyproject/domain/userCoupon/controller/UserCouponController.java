package com.example.aironyproject.domain.userCoupon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.userCoupon.dto.GetMyCouponResponse;
import com.example.aironyproject.domain.userCoupon.service.UserCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserCouponController {

	private final UserCouponService userCouponService;

	@GetMapping("/api/coupon/me")
	public ResponseEntity<CommonApiResponse<List<GetMyCouponResponse>>> getMyCoupons(){
		List<GetMyCouponResponse> data = userCouponService.getMyCoupon();

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "내 쿠폰 목록 조회 성공", data));
	}
}
