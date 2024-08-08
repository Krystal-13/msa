package com.sparta.msa_exam.product;

import lombok.Getter;

@Getter
public class ProductRequestDto {

    private String name;
    private Integer supplyPrice;
    private Integer quantity;
}
