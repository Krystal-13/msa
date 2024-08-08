package com.sparta.msa_exam.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, String userId) {

        //TODO 사용자 검증 로직

        Product product = Product.createProduct(request, userId);
        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.entityToDto(savedProduct);

    }

    public Page<ProductResponseDto> getProducts(ProductSearchDto request, Pageable pageable) {

        return productRepository.searchProducts(request, pageable);
    }

    public ProductResponseDto getProductById(Long id) {

        Product product = getProduct(id);

        return ProductResponseDto.entityToDto(product);
    }

    @Transactional
    public void reduceProductStock(Long id, int quantity) {

        Product product = getProduct(id);

        if (product.getStockQuantity() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Not enough quantity for product ID : " + id);
        }

        product.reduceStock(quantity);
        productRepository.save(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found or has been deleted"));
    }
}
