package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer supplyPrice;
    private Integer stockQuantity;
    private String createdBy;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(String name, Integer supplyPrice, Integer stockQuantity, String createdBy) {
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.stockQuantity = stockQuantity;
        this.createdBy = createdBy;
    }

    public static Product createProduct(ProductRequestDto request, String userId) {
        return Product.builder()
                .name(request.getName())
                .supplyPrice(request.getSupplyPrice())
                .stockQuantity(request.getQuantity())
                .createdBy(userId)
                .build();
    }
}
