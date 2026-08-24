package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.cart.CartItemResponse;
import com.diregebeya.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * productId/productName/productImageUrl are pulled from the nested Product
 * via dot-notation source paths - MapStruct generates the null-safe
 * navigation (item.getProduct() == null ? null : item.getProduct().getId())
 * itself. lineTotal has no source field at all - it's a computed value, so
 * it's an {@code expression} instead of a {@code source}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "lineTotal",
            expression = "java(item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponse toResponse(CartItem item);
}
