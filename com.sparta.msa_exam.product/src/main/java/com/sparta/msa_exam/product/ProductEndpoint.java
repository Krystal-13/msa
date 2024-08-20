package com.sparta.msa_exam.product;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @RabbitListener(queues = "${messaging.queues.product}")
    public void reduceProductQuantity(ProductDeliveryMessage productDeliveryMessage) {
        productService.reduceProductQuantity(productDeliveryMessage);
    }
}
