package com.sparta.msa_exam.order;

import com.sparta.msa_exam.product.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private String userId;

    private String createdBy;
    private String updatedBy;
    private String deletedBy;

    @Builder
    public Order(String name, OrderStatus status, List<OrderItem> orderItems, String userId, String createdBy, String updatedBy, String deletedBy) {
        this.name = name;
        this.status = status;
        this.orderItems = orderItems;
        this.userId = userId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static Order createOrder(String userId) {

        return Order.builder()
                .name(createOrderName())
                .status(OrderStatus.CREATED)
                .userId(userId)
                .build();
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    private static String createOrderName() {

        int randomSixNumber = (int) (Math.random() * 899999) + 100000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        return today + randomSixNumber;
    }

    public void updateOrder(List<OrderItem> orderItems, String userId, OrderStatus status) {
        this.orderItems = orderItems;
        this.status = status;
        this.updatedBy = userId;
    }
}
