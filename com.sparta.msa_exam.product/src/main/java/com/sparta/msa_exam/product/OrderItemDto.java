package com.sparta.msa_exam.product;

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
}
