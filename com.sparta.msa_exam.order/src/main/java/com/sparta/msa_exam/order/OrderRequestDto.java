package com.sparta.msa_exam.order;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {

    private List<OrderItemDto> orderItems;

    private String status;
}
