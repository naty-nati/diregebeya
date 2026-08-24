package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.order.OrderResponse;
import com.diregebeya.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.fullName")
    @Mapping(target = "userEmail", source = "user.email")
    OrderResponse toResponse(Order order);
}
