package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.domain.Product;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void reduceProductQuantity(ProductDeliveryMessage productDeliveryMessage) {

        for(OrderItemDto orderItem : productDeliveryMessage.getOrderItems()) {
            Long productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();

            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                rollbackToOrder(productDeliveryMessage, ErrorType.PRODUCT_NOT_FOUND);
                log.info("productId : " + productId + " Not found");
                return;
            }

            Product product = optionalProduct.get();
            if (product.getStockQuantity() < quantity) {
                rollbackToOrder(productDeliveryMessage, ErrorType.INSUFFICIENT_STOCK);
                log.info("INSUFFICIENT_STOCK_ERROR { "
                        + "productId : " + productId
                        + " request : " + quantity
                        + " stock : " + product.getStockQuantity() + " }");
                return;
            }

            product.reduceStockQuantity(quantity);
        }
    }

    private void rollbackToOrder(ProductDeliveryMessage productDeliveryMessage,
                                 ErrorType errorType
    ) {
        productDeliveryMessage.setErrorType(errorType);
        rabbitTemplate.convertAndSend(productErrorQueue, productDeliveryMessage);
    }
}
