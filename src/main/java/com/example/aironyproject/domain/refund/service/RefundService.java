package com.example.aironyproject.domain.refund.service;

import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.refund.entity.Refund;
import com.example.aironyproject.domain.refund.enums.RefundStatus;
import com.example.aironyproject.domain.refund.repository.RefundRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundService {

  private final RefundRepository refundRepository;

  @Transactional
  public Refund createRefund(Payment payment, int refundAmount, String reason) {
    Refund refund = new Refund(
        payment,
        refundAmount,
        reason,
        RefundStatus.COMPLETED,
        LocalDateTime.now()
    );

    return refundRepository.save(refund);
  }
}
