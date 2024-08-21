package com.sparta.msa_exam.product.dto;

import lombok.Getter;

@Getter
public class ProductRequest {

    private String name;
    private Integer supplyPrice;
    private Integer quantity;
}
