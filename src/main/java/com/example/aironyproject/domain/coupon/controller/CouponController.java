package com.example.aironyproject.domain.coupon.controller;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.coupon.dto.GetCouponResponse;
import com.example.aironyproject.domain.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@Transactional(readOnly = true)
	@GetMapping
	public ResponseEntity<CommonApiResponse<List<GetCouponResponse>>> getCoupons(){
		List<GetCouponResponse> data = couponService.getCoupon();

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "쿠폰 목록 조회 성공", data));
	}
}
