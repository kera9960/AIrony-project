package com.example.aironyproject.domain.refund.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.refund.enums.RefundStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refunds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Column(name = "refund_amount", nullable = false)
  private int refundAmount;

  @Column(nullable = false, length = 255)
  private String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RefundStatus status;

  @Column(name = "refunded_at", nullable = false)
  private LocalDateTime refundedAt;

  public Refund(
      Payment payment,
      int refundAmount,
      String reason,
      RefundStatus status,
      LocalDateTime refundedAt) {
    this.payment = payment;
    this.refundAmount = refundAmount;
    this.reason = reason;
    this.status = status;
    this.refundedAt = refundedAt;
  }
}
