package com.diregebeya.backend.repository;

import com.diregebeya.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    long countByProductId(Long productId);

    /** Null when the product has zero reviews - callers default it to 0.0. */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double averageRatingByProductId(@Param("productId") Long productId);

    /**
     * One grouped query for many products instead of the 3-query-per-product
     * pattern {@link #averageRatingByProductId} + {@link #countByProductId}
     * would take if called in a loop - this is what backs the bulk
     * /api/products/reviews/summary endpoint so a shop listing page doesn't
     * fire one review-summary request (and 3 SQL queries) per product.
     */
    @Query("SELECT r.product.id AS productId, AVG(r.rating) AS avgRating, COUNT(r) AS reviewCount "
            + "FROM Review r WHERE r.product.id IN :productIds GROUP BY r.product.id")
    List<ReviewSummaryProjection> summarizeByProductIds(@Param("productIds") Collection<Long> productIds);

    interface ReviewSummaryProjection {
        Long getProductId();
        Double getAvgRating();
        Long getReviewCount();
    }
}
