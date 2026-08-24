package com.diregebeya.backend.dto.product;

import java.math.BigDecimal;

/**
 * Every field is nullable-by-design: an absent filter means "don't filter
 * on this". Kept as a plain record instead of individual method params on
 * the service so adding a new filter later (e.g. "inStock") touches this one
 * type instead of every layer's method signature.
 */
public record ProductSearchCriteria(
        String search,
        Long categoryId,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
