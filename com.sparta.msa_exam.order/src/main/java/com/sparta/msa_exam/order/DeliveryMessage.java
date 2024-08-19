package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.dto.OrderItemDto;
import lombok.*;

import java.util.List;


@Getter
@NoArgsConstructor
@ToString
public class DeliveryMessage {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private ErrorType errorType;

    @Builder(access = AccessLevel.PRIVATE)
    public DeliveryMessage(Long orderId, Long userId, List<OrderItemDto> orderItems, ErrorType errorType) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.errorType = errorType;
    }

    public static DeliveryMessage entityToMessage(Order order) {
        List<OrderItemDto> orderItems =
                order.getOrderItems().stream().map(OrderItemDto::entityToDto).toList();

        return DeliveryMessage.builder()
                .orderId(order.getId())
                .userId(Long.valueOf(order.getUserId()))
                .orderItems(orderItems)
                .build();
    }
}
