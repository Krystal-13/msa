package com.sparta.msa_exam.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    /**
     * 조건 1. 판매자만 등록할 수 있음
     *
     * @param request 사용자 입력 값
     * @param userId gateway 에서 검증 후 넘어오는 값
     */
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto request, String userId) {

        return productService.createProduct(request, userId);
    }

    /**
     *
     * @param request 검색 조건 , 정렬 기준
     */
    @GetMapping
    public Page<ProductResponseDto> getProducts(ProductSearchDto request, Pageable pageable) {
        System.out.println(request.getName());
        System.out.println(request.getMaxPrice());
        System.out.println(request.getMinPrice());
        return productService.getProducts(request, pageable);
    }

}
