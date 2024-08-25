package com.sparta.msa_exam.payment.type;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS,
    FAILURE,
    PENDING
}
