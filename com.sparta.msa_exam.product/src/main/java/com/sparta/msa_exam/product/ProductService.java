package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.domain.Product;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponseDto createProduct(ProductRequestDto request, String userId) {

        Product product = Product.createProduct(request, userId);
        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.entityToDto(savedProduct);

    }

    @Cacheable(cacheNames = "products" ,key = "methodName")
    public List<ProductResponseDto> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(ProductResponseDto::entityToDto).toList();
    }
}
