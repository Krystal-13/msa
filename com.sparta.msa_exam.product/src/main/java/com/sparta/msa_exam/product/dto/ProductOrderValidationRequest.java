package com.sparta.msa_exam.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductOrderValidationRequest {

    List<ProductItemDto> orderItems;
    public ProductOrderValidationRequest(List<ProductItemDto> orderItems) {
        this.orderItems = orderItems;
    }
}
