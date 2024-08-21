package com.sparta.msa_exam.product.controller;

import com.sparta.msa_exam.product.service.ProductService;
import com.sparta.msa_exam.product.dto.ProductRequest;
import com.sparta.msa_exam.product.dto.ProductResponse;
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
}
