package com.sparta.msa_exam.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductItemResponse {
    private Long productId;
    private Integer supplyPrice;

    @Builder
    public ProductItemResponse(Long productId, Integer supplyPrice) {
        this.productId = productId;
        this.supplyPrice = supplyPrice;
    }
}
