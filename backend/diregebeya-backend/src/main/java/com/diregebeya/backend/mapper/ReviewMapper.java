package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.review.ReviewRequest;
import com.diregebeya.backend.dto.review.ReviewResponse;
import com.diregebeya.backend.entity.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.fullName")
    ReviewResponse toResponse(Review review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ReviewRequest request, @MappingTarget Review review);
}
