package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.review.ReviewRequest;
import com.diregebeya.backend.dto.review.ReviewResponse;
import com.diregebeya.backend.dto.review.ReviewSummaryResponse;
import com.diregebeya.backend.security.UserPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nested under /api/products/{productId}/reviews, same reasoning as
 * ProductImageController - a review never exists independently of the
 * product it's about.
 */
@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "List a product's reviews")
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviews(productId, pageable));
    }

    @Operation(summary = "Get a product's average rating and review count")
    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryResponse> getSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getSummary(productId));
    }

    @Operation(summary = "Leave a review for a product (one per customer per product)")
    @PostMapping
    public ResponseEntity<ReviewResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long productId,
                                                   @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(principal.getId(), productId, request));
    }

    @Operation(summary = "Update your own review")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long productId,
                                                   @PathVariable Long reviewId,
                                                   @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(principal.getId(), productId, reviewId, request));
    }

    @Operation(summary = "Delete your own review")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long productId,
                                         @PathVariable Long reviewId) {
        reviewService.deleteReview(principal.getId(), productId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
