package com.example.aironyproject.domain.payment.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.domain.payment.enums.PaymentStatus;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false, unique = true)
  private Reservation reservation;

  @Column(name = "payment_number", nullable = false, unique = true, length = 50)
  private String paymentNumber;

  @Column(nullable = false)
  private int amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(name = "paid_at", nullable = false)
  private LocalDateTime paidAt;

  /**
   * 가상 결제 정보를 생성
   * 실제 PG 연동 없이 예약 금액을 결제 완료 상태로 저장
   */
  public Payment(Reservation reservation, String paymentNumber, int amount, PaymentStatus status, LocalDateTime paidAt) {
    this.reservation = reservation;
    this.paymentNumber = paymentNumber;
    this.amount = amount;
    this.status = status;
    this.paidAt = paidAt;
  }

  /**
   * 결제 상태를 환불 완료 상태로 변경
   * 예약 취소 시 호출
   */
  public void refund() {
    this.status = PaymentStatus.REFUNDED;
  }
}
