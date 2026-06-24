package com.example.aironyproject.domain.userCoupon.repository;

import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByIdAndUser_Id(Long userCouponId, Long userId);
}
