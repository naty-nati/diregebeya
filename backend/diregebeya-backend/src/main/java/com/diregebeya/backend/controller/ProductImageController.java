package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.product.ProductImageRequest;
import com.diregebeya.backend.dto.product.ProductImageResponse;
import com.diregebeya.backend.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Nested under /api/products/{productId}/images rather than a flat
 * /api/product-images - a gallery image never makes sense outside the
 * context of its product, so the URL says so.
 */
@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "Product image gallery management")
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(summary = "List a product's gallery images")
    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImages(productId));
    }

    @Operation(summary = "Add a gallery image to a product (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(@PathVariable Long productId,
                                                           @Valid @RequestBody ProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productImageService.addImage(productId, request));
    }

    @Operation(summary = "Remove a gallery image from a product (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productImageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
