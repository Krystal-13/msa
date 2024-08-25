package com.sparta.msa_exam.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductItemDto {

    private Long productId;
    private Integer quantity;

    public ProductItemDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
