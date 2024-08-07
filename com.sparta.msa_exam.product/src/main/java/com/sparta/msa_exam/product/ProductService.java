package com.sparta.msa_exam.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto request, String userId) {

        //TODO 사용자 검증 로직

        Product product = Product.createProduct(request, userId);
        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.entityToDto(savedProduct);

    }
}
