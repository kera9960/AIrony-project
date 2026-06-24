package com.example.aironyproject.domain.coupon.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aironyproject.domain.coupon.dto.GetCouponResponse;
import com.example.aironyproject.domain.coupon.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;

	@Transactional(readOnly = true)
	public List<GetCouponResponse> getCoupon(){
		return couponRepository.findAll().stream()
			.map(GetCouponResponse::new)
			.toList();
	}
}
