package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.dto.OrderDeliveryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    @RabbitListener(queues = "${messaging.queues.orderError}")
    public void handleErrorMessage(OrderDeliveryMessage orderDeliveryMessage) {
        orderService.handleErrorMessage(orderDeliveryMessage);
    }
}
