package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.config.FeignClientConfig;
import com.sparta.msa_exam.product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/products")
    List<ProductResponse> getProducts();
}
