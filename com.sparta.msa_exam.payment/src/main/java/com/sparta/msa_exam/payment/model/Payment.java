package com.sparta.msa_exam.payment.model;

import com.sparta.msa_exam.payment.type.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Long orderId;
    private Integer totalPrice;
    private PaymentStatus status;
    private LocalDateTime payAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Payment(String userId, Long orderId, Integer totalPrice, PaymentStatus status, LocalDateTime payAt) {
        this.userId = userId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.payAt = payAt;
    }

    public static Payment createPayment(String userId, Long orderId, Integer totalPrice, PaymentStatus status) {
        return Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .totalPrice(totalPrice)
                .status(status)
                .payAt(LocalDateTime.now())
                .build();
    }
}
