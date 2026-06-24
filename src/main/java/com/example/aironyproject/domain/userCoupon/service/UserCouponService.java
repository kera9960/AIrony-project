package com.example.aironyproject.domain.userCoupon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.userCoupon.dto.GetMyCouponResponse;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import com.example.aironyproject.domain.userCoupon.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCouponService {

	private final UserCouponRepository userCouponRepository;

	public List<GetMyCouponResponse> getMyCoupon(Long userId){
		UserCoupon userCoupon = userCouponRepository.findByUserId(userId);
		if(userCoupon != null){
			return userCouponRepository.findAll().stream()
				.map(GetMyCouponResponse::new)
				.toList();
		}
		throw new CustomException(ErrorCode.USER_COUPON_NOT_FOUND);
	}
}
