package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.product.ProductImageRequest;
import com.diregebeya.backend.dto.product.ProductImageResponse;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.entity.ProductImage;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.ProductImageMapper;
import com.diregebeya.backend.repository.ProductImageRepository;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public List<ProductImageResponse> getImages(Long productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductImageResponse addImage(Long productId, ProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", "id", productId));

        ProductImage image = productImageMapper.toEntity(request);
        image.setProduct(product);

        return productImageMapper.toResponse(productImageRepository.save(image));
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .filter(candidate -> candidate.getProduct().getId().equals(productId))
                .orElseThrow(() -> ResourceNotFoundException.of("ProductImage", "id", imageId));

        productImageRepository.delete(image);
    }
}
