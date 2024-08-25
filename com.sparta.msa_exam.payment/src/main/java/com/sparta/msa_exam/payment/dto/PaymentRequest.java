package com.sparta.msa_exam.payment.dto;

import lombok.Getter;

@Getter
public class PaymentRequest {

    private String userId;
    private Long orderId;
    private Integer totalPrice;

    public PaymentRequest(String userId, Long orderId, Integer totalPrice) {
        this.userId = userId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }
}
