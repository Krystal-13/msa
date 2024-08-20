package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.ErrorType;
import com.sparta.msa_exam.order.domain.Order;
import lombok.*;

import java.util.List;


@Getter
public class OrderDeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private ErrorType errorType;

    @Builder(access = AccessLevel.PRIVATE)
    public OrderDeliveryMessage(Long orderId, Long userId, List<OrderItemDto> orderItems, ErrorType errorType) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.errorType = errorType;
    }

    public static OrderDeliveryMessage entityToMessage(Order order) {
        List<OrderItemDto> orderItems =
                order.getOrderItems().stream().map(OrderItemDto::entityToDto).toList();

        return OrderDeliveryMessage.builder()
                .orderId(order.getId())
                .userId(Long.valueOf(order.getUserId()))
                .orderItems(orderItems)
                .build();
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
