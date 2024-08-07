package com.sparta.msa_exam.product;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.msa_exam.product.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<ProductResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        List<Product> results = queryFactory
                .selectFrom(product)
                .where(
                        nameContains(searchDto.getName()),
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice())
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        System.out.println(results.get(0).getCreatedAt());
        
        List<ProductResponseDto> content = results.stream()
                .map(ProductResponseDto::entityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, results.size());
    }


    private BooleanExpression nameContains(String name) {
        return name != null ? product.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression priceBetween(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return product.supplyPrice.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return product.supplyPrice.goe(minPrice);
        } else if (maxPrice != null) {
            return product.supplyPrice.loe(maxPrice);
        } else {
            return null;
        }
    }


    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            com.querydsl.core.types.Order direction = sortOrder.isAscending()
                    ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
            switch (sortOrder.getProperty()) {
                case "date" -> orders.add(new OrderSpecifier<>(direction, product.createdAt));
                case "price" -> orders.add(new OrderSpecifier<>(direction, product.supplyPrice));
                default -> {
                }
            }
        }

        return orders;
    }
}
