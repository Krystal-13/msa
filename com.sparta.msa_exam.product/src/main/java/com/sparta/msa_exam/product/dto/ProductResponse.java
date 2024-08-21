package com.sparta.msa_exam.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class ProductResponse implements Serializable {

    private Long id;
    private String name;
    private Integer supplyPrice;
    private Integer quantity;

    @Builder
    private ProductResponse(Long id, String name, Integer supplyPrice, Integer quantity) {
        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.quantity = quantity;
    }
}
