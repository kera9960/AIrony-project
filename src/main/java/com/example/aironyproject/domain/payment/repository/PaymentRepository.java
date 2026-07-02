package com.example.aironyproject.domain.payment.repository;

import com.example.aironyproject.domain.payment.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByReservationId(Long reservationId);
}
