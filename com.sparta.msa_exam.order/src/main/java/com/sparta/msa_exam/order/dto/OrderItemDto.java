package com.sparta.msa_exam.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemDto {

    private Long productId;
    private Integer quantity;

    @Builder
    public OrderItemDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
