package com.sparta.msa_exam.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, String userId) {


        Product product = Product.createProduct(request, userId);
        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.entityToDto(savedProduct);

    }

    public List<ProductResponseDto> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(ProductResponseDto::entityToDto).toList();
    }
}
