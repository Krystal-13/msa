package com.sparta.msa_exam.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentDeliveryMessage {
    private Long orderId;
    private String userId;
    private String payResult;
    private String errorType;

    @Builder
    public PaymentDeliveryMessage(Long orderId, String userId, String payResult, String errorType) {
        this.orderId = orderId;
        this.userId = userId;
        this.payResult = payResult;
        this.errorType = errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
