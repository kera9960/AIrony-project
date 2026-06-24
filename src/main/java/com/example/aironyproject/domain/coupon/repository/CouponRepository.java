package com.example.aironyproject.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aironyproject.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
