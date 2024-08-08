package com.sparta.msa_exam.order;

import com.sparta.msa_exam.product.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, String userId) {

        //TODO 사용자 검증 로직

        List<OrderItemDto> orderItemDtos = request.getOrderItems();
        isStockAvailable(orderItemDtos);
        reduceStock(orderItemDtos);
        //TODO 중간에 하나라도 감량처리가 안되었을 경우 핸들링

        Order order = Order.createOrder(userId);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = OrderItem.createOrderItems(savedOrder, request.getOrderItems());
        orderItemRepository.saveAll(orderItems);

        order.addOrderItems(orderItems);

        return OrderResponseDto.entityToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId, String userId) {

        //TODO 사용자 검증 로직

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        return OrderResponseDto.entityToDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto request, String userId) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        List<OrderItemDto> orderItemDtos = request.getOrderItems();
        isStockAvailable(orderItemDtos);
        reduceStock(orderItemDtos);
        //TODO 중간에 하나라도 감량처리가 안되었을 경우 핸들링

        List<OrderItem> orderItems = OrderItem.createOrderItems(order, request.getOrderItems());
        order.updateOrder(orderItems, userId, OrderStatus.valueOf(request.getStatus()));
        Order updatedOrder = orderRepository.save(order);

        return OrderResponseDto.entityToDto(updatedOrder);
    }

    private Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
    }

    private static void validateOrderUser(String userId, Order order) {
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the order");
        }
    }

    private void isStockAvailable(List<OrderItemDto> orderItemDtos) {
        for (OrderItemDto orderItem : orderItemDtos) {
            ProductResponseDto product = productClient.getProduct(orderItem.getProductId());
            if (product.getQuantity() < orderItem.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + product.getId() + " is out of stock.");
            }
        }
    }

    private void reduceStock(List<OrderItemDto> orderItemDtos) {
        for (OrderItemDto orderItemDto : orderItemDtos) {
            productClient.reduceProductStock(orderItemDto.getProductId(), orderItemDto.getQuantity());
        }
    }
}
