package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private String userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder(access = AccessLevel.PRIVATE)
    public Order(String name, List<OrderItem> orderItems, String userId, OrderStatus orderStatus) {
        this.name = name;
        this.orderItems = orderItems;
        this.userId = userId;
        this.orderStatus = orderStatus;
    }

    public static Order createOrder(String userId) {

        return Order.builder()
                .name(createOrderName())
                .userId(userId)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    private static String createOrderName() {

        int randomSixNumber = (int) (Math.random() * 899999) + 100000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        return today + randomSixNumber;
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
