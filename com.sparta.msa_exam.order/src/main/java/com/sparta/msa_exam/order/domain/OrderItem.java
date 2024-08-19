package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;
    private Integer quantity;

    @Builder
    public OrderItem(Order order, Long productId, Integer quantity) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static OrderItem createOrderItem(Order order, OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .order(order)
                .productId(orderItemDto.getProductId())
                .quantity(orderItemDto.getQuantity())
                .build();
    }

    public static List<OrderItem> createOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto orderItemDto : orderItemDtos) {
            orderItems.add(createOrderItem(order, orderItemDto));
        }

        return orderItems;
    }

    public void increaseOrderItemQuantity(int quantity) {
        this.quantity += quantity;
    }
}
