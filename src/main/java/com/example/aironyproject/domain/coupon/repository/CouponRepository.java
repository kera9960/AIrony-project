package com.example.aironyproject.domain.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.aironyproject.domain.coupon.entity.Coupon;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c FROM Coupon c WHERE c.id = :couponId ")
	Optional<Coupon> findByIdWithLock(@Param("couponId") Long couponId);
}
