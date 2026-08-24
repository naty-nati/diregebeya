package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.order.OrderItemResponse;
import com.diregebeya.backend.entity.OrderItem;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

/**
 * productName/unitPrice map straight across from OrderItem's own snapshotted
 * columns (not the live product, unlike CartItemMapper) - only productId
 * and the computed lineTotal need special handling here.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "lineTotal",
            expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderItemResponse toResponse(OrderItem item);
}
