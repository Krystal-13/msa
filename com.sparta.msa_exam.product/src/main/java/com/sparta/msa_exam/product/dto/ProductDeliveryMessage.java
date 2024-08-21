package com.sparta.msa_exam.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductDeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private String errorType;
    private Integer totalPrice;

    @Builder(access = AccessLevel.PRIVATE)
    public ProductDeliveryMessage(Long orderId, Long userId, List<OrderItemDto> orderItems, String errorType, Integer totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.errorType = errorType;
        this.totalPrice = totalPrice;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
