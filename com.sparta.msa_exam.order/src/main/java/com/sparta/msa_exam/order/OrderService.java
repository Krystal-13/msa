package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderItem;
import com.sparta.msa_exam.order.dto.OrderItemDto;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, String userId) {

        List<OrderItemDto> orderItemDtos = request.getOrderItems();
        List<ProductResponseDto> products = productClient.getProducts();
        List<Long> list = products.stream().map(ProductResponseDto::getId).toList();

        Order order = Order.createOrder(userId);
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto orderItemDto : orderItemDtos) {
            if (list.contains(orderItemDto.getProductId())) {
                orderItems.add(OrderItem.createOrderItem(savedOrder, orderItemDto));
            } else {
                log.info("Product not found");
                //TODO 상품이 존재하지 않을 경우 처리 로직
            }
        }

        orderItemRepository.saveAll(orderItems);
        order.addOrderItems(orderItems);

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
}
