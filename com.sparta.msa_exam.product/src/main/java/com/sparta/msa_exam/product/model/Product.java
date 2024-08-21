package com.sparta.msa_exam.product.model;

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

    public static Product createProduct(String name, Integer price, Integer quantity, String userId) {
        return Product.builder()
                .name(name)
                .supplyPrice(price)
                .stockQuantity(quantity)
                .createdBy(userId)
                .build();
    }

    public void reduceStockQuantity(Integer quantity) {
        this.stockQuantity -= quantity;
    }
}
