package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.product.ProductImageRequest;
import com.diregebeya.backend.dto.product.ProductImageResponse;
import com.diregebeya.backend.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageRequest request);

    ProductImageResponse toResponse(ProductImage image);
}
