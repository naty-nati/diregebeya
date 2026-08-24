package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.review.ReviewRequest;
import com.diregebeya.backend.dto.review.ReviewResponse;
import com.diregebeya.backend.dto.review.ReviewSummaryResponse;
import com.diregebeya.backend.entity.Review;
import com.diregebeya.backend.exception.DuplicateResourceException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.ReviewMapper;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.repository.ReviewRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Page<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        ensureProductExists(productId);
        return reviewRepository.findByProductId(productId, pageable).map(reviewMapper::toResponse);
    }

    @Override
    public ReviewSummaryResponse getSummary(Long productId) {
        ensureProductExists(productId);

        Double average = reviewRepository.averageRatingByProductId(productId);
        long count = reviewRepository.countByProductId(productId);

        return ReviewSummaryResponse.builder()
                .averageRating(average == null ? 0.0 : average)
                .reviewCount(count)
                .build();
    }

    @Override
    public Map<Long, ReviewSummaryResponse> getSummaries(Collection<Long> productIds) {
        Map<Long, ReviewSummaryResponse> summaries = new LinkedHashMap<>();
        if (productIds.isEmpty()) {
            return summaries;
        }

        for (Long productId : productIds) {
            summaries.put(productId, ReviewSummaryResponse.builder()
                    .averageRating(0.0)
                    .reviewCount(0)
                    .build());
        }

        for (ReviewRepository.ReviewSummaryProjection row : reviewRepository.summarizeByProductIds(productIds)) {
            summaries.put(row.getProductId(), ReviewSummaryResponse.builder()
                    .averageRating(row.getAvgRating() == null ? 0.0 : row.getAvgRating())
                    .reviewCount(row.getReviewCount())
                    .build());
        }

        return summaries;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, Long productId, ReviewRequest request) {
        if (!productRepository.existsById(productId)) {
            throw ResourceNotFoundException.of("Product", "id", productId);
        }
        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(userRepository.getReferenceById(userId));
        review.setProduct(productRepository.getReferenceById(productId));

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long userId, Long productId, Long reviewId, ReviewRequest request) {
        Review review = findOwnedReviewOrThrow(userId, productId, reviewId);
        reviewMapper.updateEntityFromRequest(request, review);

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long productId, Long reviewId) {
        Review review = findOwnedReviewOrThrow(userId, productId, reviewId);
        reviewRepository.delete(review);
    }

    private Review findOwnedReviewOrThrow(Long userId, Long productId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ResourceNotFoundException.of("Review", "id", reviewId));

        if (!review.getProduct().getId().equals(productId) || !review.getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("Review", "id", reviewId);
        }
        return review;
    }

    private void ensureProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw ResourceNotFoundException.of("Product", "id", productId);
        }
    }
}
