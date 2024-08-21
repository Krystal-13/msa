package com.sparta.msa_exam.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class OrderDeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private String errorType;

    @Builder
    public OrderDeliveryMessage(Long orderId, Long userId, List<OrderItemDto> orderItems, String errorType) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.errorType = errorType;
    }
}
