package com.example.aironyproject.domain.userCoupon.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.aironyproject.domain.userCoupon.dto.GetMyCouponResponse;
import com.example.aironyproject.domain.userCoupon.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCouponService {

	private final UserCouponRepository userCouponRepository;

	@Transactional(readOnly = true)
	public List<GetMyCouponResponse> getMyCoupon(Long userId){
		return userCouponRepository.findByUserId(userId).stream()
			.map(GetMyCouponResponse::from)
			.toList();
	}

}
