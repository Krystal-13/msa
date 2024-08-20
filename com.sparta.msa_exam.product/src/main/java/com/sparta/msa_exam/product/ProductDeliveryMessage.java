package com.sparta.msa_exam.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductDeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private ErrorType errorType;
    private Integer totalPrice;

    @Builder(access = AccessLevel.PRIVATE)
    public ProductDeliveryMessage(Long orderId, Long userId, List<OrderItemDto> orderItems, ErrorType errorType, Integer totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.errorType = errorType;
        this.totalPrice = totalPrice;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
