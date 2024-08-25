package com.sparta.msa_exam.order.model;

import com.sparta.msa_exam.order.type.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private String userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Integer totalPrice;

    @Builder(access = AccessLevel.PRIVATE)
    public Order(String name, List<OrderItem> orderItems, String userId, OrderStatus orderStatus, Integer totalPrice) {
        this.name = name;
        this.orderItems = orderItems;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }

    public static Order createOrder(String userId, String name) {
        return Order.builder()
                .name(name)
                .userId(userId)
                .build();
    }

    public void setupOrder(List<OrderItem> orderItems, Integer totalPrice) {
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.PENDING;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
