package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderItem;
import com.sparta.msa_exam.order.dto.OrderDeliveryMessage;
import com.sparta.msa_exam.order.dto.OrderItemDto;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Value("${messaging.queues.product}")
    private String queueProduct;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, String userId) {

        Order order = Order.createOrder(userId);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = OrderItem.createOrderItems(order, request.getOrderItems());
        orderItemRepository.saveAll(orderItems);
        savedOrder.addOrderItems(orderItems);

        OrderDeliveryMessage orderDeliveryMessage = OrderDeliveryMessage.entityToMessage(savedOrder);
        rabbitTemplate.convertAndSend(queueProduct, orderDeliveryMessage);

        return OrderResponseDto.entityToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "order", key = "args[0]")
    public OrderResponseDto getOrder(Long orderId, String userId) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        return OrderResponseDto.entityToDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(
            Long orderId, OrderRequestDto orderRequest, String userId
    ) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        List<OrderItemDto> requestOrderItems = orderRequest.getOrderItems();

        List<ProductResponseDto> productResponseDtos = productClient.getProducts();
        List<Long> list =
                productResponseDtos.stream().map(ProductResponseDto::getId).toList();

        for (OrderItemDto requestOrderItem : requestOrderItems) {
            if (list.contains(requestOrderItem.getProductId())) {
                Optional<OrderItem> optionalOrderItem =
                        orderItemRepository.findByProductIdAndOrder(
                                requestOrderItem.getProductId(), order
                        );

                if (optionalOrderItem.isEmpty()) {
                    OrderItem orderItem = OrderItem.createOrderItem(order, requestOrderItem);
                    OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                    order.getOrderItems().add(savedOrderItem);
                } else {
                    optionalOrderItem.get()
                            .increaseOrderItemQuantity(requestOrderItem.getQuantity());
                }

            } else {
                log.info("Product not found");
                //TODO 상품이 존재하지 않을 경우 처리 로직
            }
        }

        return OrderResponseDto.entityToDto(order);
    }

    private Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found or has been deleted"
                        ));
    }

    private static void validateOrderUser(String userId, Order order) {
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized access to the order"
            );
        }
    }

    @Transactional
    public void handleErrorMessage(OrderDeliveryMessage orderDeliveryMessage) {

        Optional<Order> optionalOrder = orderRepository.findById(orderDeliveryMessage.getOrderId());
        if (optionalOrder.isEmpty()) {
            log.info("orderId : " + orderDeliveryMessage.getOrderId() + " Not found");
            return;
        }

        Order order = optionalOrder.get();
        order.setOrderStatus(OrderStatus.CANCELLED);
    }
}
