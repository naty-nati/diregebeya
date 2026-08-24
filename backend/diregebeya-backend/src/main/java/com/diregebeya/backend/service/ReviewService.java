package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.review.ReviewRequest;
import com.diregebeya.backend.dto.review.ReviewResponse;
import com.diregebeya.backend.dto.review.ReviewSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;

public interface ReviewService {

    Page<ReviewResponse> getReviews(Long productId, Pageable pageable);

    ReviewSummaryResponse getSummary(Long productId);

    /** Bulk form of {@link #getSummary}; missing/zero-review ids map to a 0.0/0 summary. */
    Map<Long, ReviewSummaryResponse> getSummaries(Collection<Long> productIds);

    /** One per (user, product) - throws DuplicateResourceException on a second attempt. */
    ReviewResponse createReview(Long userId, Long productId, ReviewRequest request);

    /** Ownership-checked: only the review's author can edit it. */
    ReviewResponse updateReview(Long userId, Long productId, Long reviewId, ReviewRequest request);

    /** Ownership-checked: only the review's author can delete it. */
    void deleteReview(Long userId, Long productId, Long reviewId);
}
