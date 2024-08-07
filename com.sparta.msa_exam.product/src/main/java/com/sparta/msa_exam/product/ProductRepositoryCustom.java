package com.sparta.msa_exam.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable);
}
