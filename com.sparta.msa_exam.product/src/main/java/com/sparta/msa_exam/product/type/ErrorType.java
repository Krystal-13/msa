package com.sparta.msa_exam.product.type;

import lombok.Getter;

@Getter
public enum ErrorType {
    INSUFFICIENT_STOCK(400),
    PRODUCT_NOT_FOUND(404);

    private Integer status;

    ErrorType(int status) {
        this.status = status;
    }

    public static ErrorType getErrorTypeByStatusCode(int statusCode) {
        for (ErrorType errorType : ErrorType.values()) {
            if (errorType.getStatus().equals(statusCode)) {
                return errorType;
            }
        }
        throw new IllegalArgumentException("No matching ErrorType for status code: " + statusCode);
    }
}
