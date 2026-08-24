package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.category.CategoryRequest;
import com.diregebeya.backend.dto.category.CategoryResponse;
import com.diregebeya.backend.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    /**
     * Used by update (PUT): mutates the already-loaded managed entity in
     * place instead of building a new one, so JPA's dirty-checking issues a
     * single UPDATE on flush - no need to call save() explicitly, and the
     * id/generated columns are never touched by client input.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
