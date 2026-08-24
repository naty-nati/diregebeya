package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.product.ProductRequest;
import com.diregebeya.backend.dto.product.ProductResponse;
import com.diregebeya.backend.dto.product.ProductSearchCriteria;
import com.diregebeya.backend.dto.review.ReviewSummaryResponse;
import com.diregebeya.backend.service.ProductService;
import com.diregebeya.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog browsing and management")
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    /**
     * All filters are optional query params - omitting every one returns
     * the full catalog, paged. {@code @PageableDefault} supplies sane
     * defaults so a client can call GET /api/products with no params at all
     * and still get a bounded, sorted result instead of every row at once.
     */
    @Operation(summary = "Search products with optional filtering, pagination, and sorting")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var criteria = new ProductSearchCriteria(search, categoryId, brand, minPrice, maxPrice);
        return ResponseEntity.ok(productService.search(criteria, pageable));
    }

    @Operation(summary = "Get a product by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Bulk form of GET /api/products/{id}/reviews/summary - a shop listing
     * page needs every visible product's rating at once, and firing one
     * request per product (each doing 3 separate SQL queries server-side)
     * is what made /shop take several seconds to load.
     */
    @Operation(summary = "Get review averages/counts for multiple products in one call")
    @GetMapping("/reviews/summary")
    public ResponseEntity<Map<Long, ReviewSummaryResponse>> getReviewSummaries(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(reviewService.getSummaries(ids));
    }

    @Operation(summary = "Create a product (admin/staff)")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @Operation(summary = "Update a product (admin/staff)")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(summary = "Delete a product (admin/staff)")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
