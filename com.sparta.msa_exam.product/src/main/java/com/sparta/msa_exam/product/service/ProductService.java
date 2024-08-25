package com.sparta.msa_exam.product.service;

import com.sparta.msa_exam.product.dto.*;
import com.sparta.msa_exam.product.repository.ProductRepository;
import com.sparta.msa_exam.product.model.Product;
import com.sparta.msa_exam.product.type.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("${messaging.queues.orderError}")
    private String productErrorQueue;

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request, String userId) {

        Product product = Product.createProduct(
                request.getName(),
                request.getSupplyPrice(),
                request.getQuantity(),
                userId
        );
        Product savedProduct = productRepository.save(product);

        return productToDto(savedProduct);

    }

    @Cacheable(cacheNames = "products" ,key = "methodName")
    public List<ProductResponse> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(this::productToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductItemResponse> validateAndGetProductInfo(List<ProductItemDto> request) {

        List<ProductItemResponse> productItemResponse = new ArrayList<>();
        for (ProductItemDto orderItem : request) {
            Long productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();

            Product product = productRepository.findById(productId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND)
            );

            if (quantity > product.getStockQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            productItemResponse.add(productToOrderItemDto(product));
        }
        return productItemResponse;
    }

    @Transactional
    public void reduceProductQuantity(ProductDeliveryMessage productDeliveryMessage) {
        for (ProductItemDto orderItem : productDeliveryMessage.getOrderItems()) {
            Long productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();

            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                log.info("productId : " + productId + " Not found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            Product product = optionalProduct.get();
            if (product.getStockQuantity() < quantity) {
                log.info("INSUFFICIENT_STOCK_ERROR { "
                        + "productId : " + productId
                        + " request : " + quantity
                        + " stock : " + product.getStockQuantity() + " }");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            product.reduceStockQuantity(quantity);
        }
    }
    public void rollbackToOrder(ProductDeliveryMessage productDeliveryMessage,
                                 ErrorType errorType
    ) {
        productDeliveryMessage.setErrorType(errorType.name());
        rabbitTemplate.convertAndSend(productErrorQueue, productDeliveryMessage);
    }

    private ProductResponse productToDto(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .supplyPrice(product.getSupplyPrice())
                .quantity(product.getStockQuantity())
                .build();
    }

    private ProductItemResponse productToOrderItemDto(Product product) {

        return ProductItemResponse.builder()
                .productId(product.getId())
                .supplyPrice((product.getSupplyPrice()))
                .build();
    }
}
