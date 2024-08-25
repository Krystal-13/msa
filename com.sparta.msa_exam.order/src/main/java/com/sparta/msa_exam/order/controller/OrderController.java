package com.sparta.msa_exam.order.controller;

import com.sparta.msa_exam.order.dto.OrderRequest;
import com.sparta.msa_exam.order.dto.OrderResponse;
import com.sparta.msa_exam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @RequestBody OrderRequest request, Principal principal
    ) {
        return orderService.createOrder(request.getOrderItems(), principal.getName());
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable("orderId") Long orderId, Principal principal
    ) {
        return orderService.getOrder(orderId, principal.getName());
    }

    @PutMapping("/{orderId}")
    public OrderResponse updateOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderRequest requestDto, Principal principal
    ) {
        return orderService.updateOrder(orderId, requestDto.getOrderItems(), principal.getName());
    }
}
