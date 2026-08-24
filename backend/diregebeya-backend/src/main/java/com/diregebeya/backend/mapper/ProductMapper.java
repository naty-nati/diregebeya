package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.product.ProductRequest;
import com.diregebeya.backend.dto.product.ProductResponse;
import com.diregebeya.backend.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * {@code uses = {CategoryMapper.class, ProductImageMapper.class}} lets
 * MapStruct delegate nested conversions - Category -> CategoryResponse and
 * List&lt;ProductImage&gt; -> List&lt;ProductImageResponse&gt; (matched by
 * the "images" field name on both sides) - to the mappers that already own
 * them, instead of duplicating that logic here.
 *
 * {@code category} is ignored on both directions from ProductRequest: the
 * request only carries a categoryId (a Long), not a Category entity, so
 * there's no mapping MapStruct could generate for it - ProductServiceImpl
 * resolves the id to a managed Category and sets it explicitly after
 * calling the mapper.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, ProductImageMapper.class})
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
