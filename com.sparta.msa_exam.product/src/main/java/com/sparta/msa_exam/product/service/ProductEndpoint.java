package com.sparta.msa_exam.product.service;

import com.sparta.msa_exam.product.dto.ProductDeliveryMessage;
import com.sparta.msa_exam.product.type.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @RabbitListener(queues = "${messaging.queues.product}")
    public void reduceProductQuantity(ProductDeliveryMessage productDeliveryMessage) {
        try {
            productService.reduceProductQuantity(productDeliveryMessage);
        } catch (ResponseStatusException e) {
            productService.rollbackToOrder(
                    productDeliveryMessage,
                    ErrorType.getErrorTypeByStatusCode(e.getStatusCode().value())
            );
        }
    }
}
