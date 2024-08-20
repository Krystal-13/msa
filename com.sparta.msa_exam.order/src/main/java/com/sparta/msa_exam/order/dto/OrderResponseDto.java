package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.OrderStatus;
import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private String name;
    private List<Long> productIds;
    private OrderStatus orderStatus;

    @Builder
    public OrderResponseDto(String name, List<Long> productIds, OrderStatus orderStatus) {
        this.name = name;
        this.productIds = productIds;
        this.orderStatus = orderStatus;
    }

    public static OrderResponseDto entityToDto(Order order) {
        return OrderResponseDto.builder()
                .name(order.getName())
                .productIds(order.getOrderItems().stream()
                        .map(OrderItem::getProductId).toList())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
