package com.example.aironyproject.domain.userCoupon.service;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.coupon.dto.GetCouponResponse;
import com.example.aironyproject.domain.user.repository.UserRepository;
import com.example.aironyproject.domain.userCoupon.dto.GetMyCouponResponse;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import com.example.aironyproject.domain.userCoupon.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCouponService {

	private final UserCouponRepository userCouponRepository;

	@Transactional(readOnly = true)
	public List<GetMyCouponResponse> getMyCoupon(){
		return userCouponRepository.findAll().stream()
			.map(GetMyCouponResponse::new)
			.toList();
	}

}
