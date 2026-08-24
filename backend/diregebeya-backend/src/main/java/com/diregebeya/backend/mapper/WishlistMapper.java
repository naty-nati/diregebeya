package com.diregebeya.backend.mapper;

import com.diregebeya.backend.dto.wishlist.WishlistItemResponse;
import com.diregebeya.backend.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "addedAt", source = "createdAt")
    WishlistItemResponse toResponse(Wishlist wishlist);
}
