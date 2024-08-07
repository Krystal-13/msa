package com.sparta.msa_exam.product;

import lombok.*;

@Getter
@Setter
public class ProductSearchDto {
    private String name;
    private Double minPrice;
    private Double maxPrice;
}
