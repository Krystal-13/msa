package com.sparta.msa_exam.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private String name;
    private List<Long> productIds;

    @Builder
    public OrderResponseDto(String name, List<Long> productIds) {
        this.name = name;
        this.productIds = productIds;
    }

    public static OrderResponseDto entityToDto(Order order) {
        return OrderResponseDto.builder()
                .name(order.getName())
                .productIds(order.getOrderItems().stream().map(OrderItem::getProductId).toList())
                .build();
    }
}
