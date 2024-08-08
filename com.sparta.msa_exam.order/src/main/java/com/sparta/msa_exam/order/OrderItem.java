package com.sparta.msa_exam.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
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

    public static List<OrderItem> createOrderItems(Order order, List<OrderItemDto> orderItemDtos) {

        if (orderItemDtos.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemDto orderItemDto : orderItemDtos) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(orderItemDto.getProductId())
                    .quantity(orderItemDto.getQuantity())
                    .build();
            orderItems.add(orderItem);
        }

        return orderItems;
    }
}
