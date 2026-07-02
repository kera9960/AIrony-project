package com.example.aironyproject.domain.refund.repository;

import com.example.aironyproject.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {

}
