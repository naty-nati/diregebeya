package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.coupon.CouponRequest;
import com.diregebeya.backend.dto.coupon.CouponResponse;
import com.diregebeya.backend.entity.Coupon;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    Coupon toEntity(CouponRequest request);

    CouponResponse toResponse(Coupon coupon);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CouponRequest request, @MappingTarget Coupon coupon);
}
