package com.sparta.msa_exam.product.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductDeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<ProductItemDto> orderItems;
    private String errorType;
    private Integer totalPrice;

    public ProductDeliveryMessage(Long orderId, Long userId, List<ProductItemDto> orderItems, String errorType, Integer totalPrice) {
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
