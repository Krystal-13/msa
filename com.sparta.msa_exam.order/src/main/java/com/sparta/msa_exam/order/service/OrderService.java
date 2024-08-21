package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.repository.OrderItemRepository;
import com.sparta.msa_exam.order.repository.OrderRepository;
import com.sparta.msa_exam.order.model.Order;
import com.sparta.msa_exam.order.model.OrderItem;
import com.sparta.msa_exam.order.type.OrderStatus;
import com.sparta.msa_exam.order.dto.OrderDeliveryMessage;
import com.sparta.msa_exam.order.dto.OrderItemDto;
import com.sparta.msa_exam.order.dto.OrderRequest;
import com.sparta.msa_exam.order.dto.OrderResponse;
import com.sparta.msa_exam.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public OrderResponse createOrder(OrderRequest request, String userId) {

        Order order = Order.createOrder(userId, createOrderName());
        Order savedOrder = orderRepository.save(order);


        List<OrderItem> orderItems = OrderItem.createOrderItems(order, extractProductIdAndQuantity(request.getOrderItems()));
        orderItemRepository.saveAll(orderItems);
        savedOrder.addOrderItems(orderItems);

        OrderDeliveryMessage orderDeliveryMessage = entityToMessage(savedOrder);
        rabbitTemplate.convertAndSend(queueProduct, orderDeliveryMessage);

        return orderToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "order", key = "args[0]")
    public OrderResponse getOrder(Long orderId, String userId) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        return orderToDto(order);
    }

    @Transactional
    public OrderResponse updateOrder(
            Long orderId, OrderRequest orderRequest, String userId
    ) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        List<OrderItemDto> requestOrderItems = orderRequest.getOrderItems();

        List<ProductResponse> productResponses = productClient.getProducts();
        List<Long> list =
                productResponses.stream().map(ProductResponse::getId).toList();

        for (OrderItemDto requestOrderItem : requestOrderItems) {
            if (list.contains(requestOrderItem.getProductId())) {
                Optional<OrderItem> optionalOrderItem =
                        orderItemRepository.findByProductIdAndOrder(
                                requestOrderItem.getProductId(), order
                        );

                if (optionalOrderItem.isEmpty()) {
                    OrderItem orderItem =
                            OrderItem.createOrderItem(
                                    order,
                                    requestOrderItem.getProductId(),
                                    requestOrderItem.getQuantity()
                            );
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

        return orderToDto(order);
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

    private String createOrderName() {

        int randomSixNumber = (int) (Math.random() * 899999) + 100000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        return today + randomSixNumber;
    }

    private Map<Long, Integer> extractProductIdAndQuantity(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .collect(Collectors.toMap(OrderItemDto::getProductId, OrderItemDto::getQuantity));
    }

    private OrderResponse orderToDto(Order order) {
        return OrderResponse.builder()
                .name(order.getName())
                .productIds(order.getOrderItems().stream()
                        .map(OrderItem::getProductId).toList())
                .orderStatus(order.getOrderStatus().name())
                .build();
    }

    public OrderItemDto orderItemToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .build();
    }

    public OrderDeliveryMessage entityToMessage(Order order) {
        List<OrderItemDto> orderItems =
                order.getOrderItems().stream().map(this::orderItemToDto).toList();

        return OrderDeliveryMessage.builder()
                .orderId(order.getId())
                .userId(Long.valueOf(order.getUserId()))
                .orderItems(orderItems)
                .build();
    }
}
