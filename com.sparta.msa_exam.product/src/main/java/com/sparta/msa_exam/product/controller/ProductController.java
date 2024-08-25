package com.sparta.msa_exam.product.controller;

import com.sparta.msa_exam.product.dto.*;
import com.sparta.msa_exam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ProductResponse createProduct(
            @RequestBody ProductRequest request, Principal principal
    ) {
        return productService.createProduct(request, principal.getName());
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/verify")
    public List<ProductItemResponse> validateAndGetProductInfo(
            @RequestBody ProductOrderValidationRequest request) {
        return productService.validateAndGetProductInfo(request.getOrderItems());
    }
}
