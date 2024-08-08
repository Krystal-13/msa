package com.sparta.msa_exam.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     *
     * @param request  주문 요청 데이터를 포함하는 DTO (ProductIds 포함)
     * @param userId   Gateway 에서 검증 후 전달된 사용자 ID
     * @return OrderResponseDto  주문의 이름, 상태, 그리고 Long 타입의 ProductIds 를 포함하는 응답 DTO
     */
    @PostMapping
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto request, String userId) {

        return orderService.createOrder(request, userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrder(@PathVariable("orderId") Long orderId, String userId) {

        return orderService.getOrder(orderId, userId);
    }

    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(@PathVariable("orderId") Long orderId,
                                        @RequestBody OrderRequestDto requestDto, String userId) {

        return orderService.updateOrder(orderId, requestDto, userId);
    }
}
