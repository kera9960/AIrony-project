package com.example.aironyproject.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.coupon.dto.GetCouponResponse;
import com.example.aironyproject.domain.coupon.entity.Coupon;
import com.example.aironyproject.domain.coupon.repository.CouponRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import com.example.aironyproject.domain.userCoupon.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;
	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;

	@Transactional(readOnly = true)
	public List<GetCouponResponse> getCoupon(){
		return couponRepository.findAll().stream()
			.map(GetCouponResponse::new)
			.toList();
	}

	@Transactional
	public GetCouponResponse registCoupon(Long userId, Long couponId){
		User user = userRepository.findById(userId).orElseThrow(
			() -> new CustomException(ErrorCode.USER_NOT_FOUND)
		);

		Coupon coupon = couponRepository.findByIdWithLock(couponId).orElseThrow(
			() -> new CustomException(ErrorCode.COUPON_NOT_FOUND)
		);

		if(userCouponRepository.existsByUser_IdAndCoupon_Id(userId, couponId)){
			throw new CustomException(ErrorCode.COUPON_ALREADY_ISSUED);
		}

		coupon.issue(LocalDateTime.now());

		UserCoupon userCoupon = new UserCoupon(user, coupon, LocalDateTime.now(), coupon.getEndedAt());
		userCouponRepository.save(userCoupon);
		return new GetCouponResponse(coupon.getName(), coupon.getDiscountAmount(), coupon.getStartedAt(), coupon.getEndedAt());
	}
}
