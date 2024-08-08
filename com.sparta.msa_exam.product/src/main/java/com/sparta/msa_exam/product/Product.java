package com.sparta.msa_exam.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer supplyPrice;
    private Integer stockQuantity;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(String name, Integer supplyPrice, Integer stockQuantity, String createdBy, String updatedBy, String deletedBy) {
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.stockQuantity = stockQuantity;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static Product createProduct(ProductRequestDto request, String userId) {
        return Product.builder()
                .name(request.getName())
                .supplyPrice(request.getSupplyPrice())
                .stockQuantity(request.getQuantity())
                .createdBy(userId)
                .build();
    }

    public void reduceStock(int quantity) {
        this.stockQuantity = this.stockQuantity - quantity;
    }
}
