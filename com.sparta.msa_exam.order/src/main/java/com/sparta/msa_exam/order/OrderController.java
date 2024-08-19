package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto createOrder(
            @RequestBody OrderRequestDto request, Principal principal
    ) {
        return orderService.createOrder(request, principal.getName());
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrder(
            @PathVariable("orderId") Long orderId, Principal principal
    ) {
        return orderService.getOrder(orderId, principal.getName());
    }

    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderRequestDto requestDto, Principal principal
    ) {
        return orderService.updateOrder(orderId, requestDto, principal.getName());
    }
}
