package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.product.ProductImageRequest;
import com.diregebeya.backend.dto.product.ProductImageResponse;

import java.util.List;

public interface ProductImageService {

    List<ProductImageResponse> getImages(Long productId);

    ProductImageResponse addImage(Long productId, ProductImageRequest request);

    void deleteImage(Long productId, Long imageId);
}
