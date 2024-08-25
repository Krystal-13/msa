package com.sparta.msa_exam.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentResponse {
    String payResult;

    public PaymentResponse(String payResult) {
        this.payResult = payResult;
    }
}
