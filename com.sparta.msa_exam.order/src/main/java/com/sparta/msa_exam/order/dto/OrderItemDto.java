package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.domain.OrderItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemDto {

    private Long productId;
    private Integer quantity;

    @Builder(access = AccessLevel.PRIVATE)
    public OrderItemDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static OrderItemDto entityToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
