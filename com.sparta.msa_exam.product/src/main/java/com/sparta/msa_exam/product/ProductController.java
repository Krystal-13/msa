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
     * 상품 등록
     *
     * @param request 사용자 입력 값
     * @param userId gateway 에서 검증 후 넘어오는 값
     */
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto request, String userId) {

        //TODO 조건 1. 판매자만 등록할 수 있음

        return productService.createProduct(request, userId);
    }

    /**
     * 상품 목록 조회
     *
     * @param request  검색 조건을 포함한 DTO
     * @param pageable 페이지 및 정렬 정보를 포함한 Pageable 객체
     * @return Page<ProductResponseDto>  페이지 처리된 제품 응답 DTO 목록
     */
    @GetMapping
    public Page<ProductResponseDto> getProducts(ProductSearchDto request, Pageable pageable) {

        return productService.getProducts(request, pageable);
    }

    /**
     * ID 로 상품 정보 조회
     *
     * @param productId  조회할 상품 ID
     * @return ProductResponseDto  조회된 상품 정보를 포함하는 응답 DTO
     */
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable("id") Long productId) {

        return productService.getProductById(productId);
    }

    /**
     * 상품의 재고 수량 감소
     *
     * @param id       재고를 줄일 상품 ID
     * @param quantity 재고를 줄일 수량
     */
    @GetMapping("/{id}/reduceQuantity")
    public void reduceProductStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity) {

        productService.reduceProductStock(id, quantity);
    }

}
