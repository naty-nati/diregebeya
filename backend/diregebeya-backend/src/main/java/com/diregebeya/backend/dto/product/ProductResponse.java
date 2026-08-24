package com.diregebeya.backend.dto.product;

import com.diregebeya.backend.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private CategoryResponse category;
    private List<ProductImageResponse> images;
    private Instant createdAt;
    private Instant updatedAt;
}
