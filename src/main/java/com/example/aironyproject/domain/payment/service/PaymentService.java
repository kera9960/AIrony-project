package com.example.aironyproject.domain.payment.service;

import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.payment.enums.PaymentStatus;
import com.example.aironyproject.domain.payment.repository.PaymentRepository;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  /**
   * 예약 생성 과정에서 가상 결제를 처리
   * 결제번호를 생성한 뒤 결제 완료 상태의 Payment를 저장
   */
  @Transactional
  public Payment createPayment(Reservation reservation, int amount) {
    String paymentNumber = generatePaymentNumber();

    Payment payment = new Payment(
        reservation,
        paymentNumber,
        amount,
        PaymentStatus.PAID,
        LocalDateTime.now()
    );

    return paymentRepository.save(payment);
  }

  /**
   * 사용자에게 노출할 결제번호를 생성
   * 형식: PyyyyMMdd-랜덤문자
   */
  private String generatePaymentNumber() {
    String date = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    String uuid = UUID.randomUUID()
        .toString()
        .substring(0,6)
        .toUpperCase();

    return "P" + date + "-" + uuid;
  }
}
