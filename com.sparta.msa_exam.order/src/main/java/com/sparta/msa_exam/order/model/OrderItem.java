package com.sparta.msa_exam.order.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static OrderItem createOrderItem(Order order, Long productId, Integer quantity) {
        return OrderItem.builder()
                .order(order)
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    public static List<OrderItem> createOrderItems(Order order, Map<Long, Integer> map) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            orderItems.add(createOrderItem(order, productId, quantity));
        }

        return orderItems;
    }

    public void increaseOrderItemQuantity(int quantity) {
        this.quantity += quantity;
    }
}
