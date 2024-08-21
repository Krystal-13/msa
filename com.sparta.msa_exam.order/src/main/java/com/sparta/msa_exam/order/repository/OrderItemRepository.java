package com.sparta.msa_exam.order.repository;

import com.sparta.msa_exam.order.model.Order;
import com.sparta.msa_exam.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByProductIdAndOrder(Long productId, Order order);
}
