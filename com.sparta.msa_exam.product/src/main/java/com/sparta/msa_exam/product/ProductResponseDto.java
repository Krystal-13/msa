package com.sparta.msa_exam.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer supplyPrice;

    @Builder(access = AccessLevel.PRIVATE)
    private ProductResponseDto(Long id, String name, Integer supplyPrice) {
        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
    }

    public static ProductResponseDto entityToDto(Product product) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .supplyPrice(product.getSupplyPrice())
                .build();
    }
}
