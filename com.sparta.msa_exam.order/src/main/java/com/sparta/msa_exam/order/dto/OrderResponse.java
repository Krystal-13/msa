package com.sparta.msa_exam.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponse {

    private String name;
    private List<Long> productIds;
    private String orderStatus;

    @Builder
    public OrderResponse(String name, List<Long> productIds, String orderStatus) {
        this.name = name;
        this.productIds = productIds;
        this.orderStatus = orderStatus;
    }
}
