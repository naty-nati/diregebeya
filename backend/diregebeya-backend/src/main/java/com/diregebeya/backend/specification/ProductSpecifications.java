package com.diregebeya.backend.specification;

import com.diregebeya.backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Each method returns null when its filter is absent, and {@link #combine}
 * drops nulls before ANDing the rest together - Specification.where(null)
 * is valid and means "no restriction", which is what lets every filter in
 * ProductSearchCriteria stay fully optional without a chain of if-statements
 * duplicated across every method that queries products.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasBrand(String brand) {
        if (!StringUtils.hasText(brand)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    /** Matches against name OR brand - the common "search box" experience. */
    public static Specification<Product> nameOrBrandContains(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        String likePattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),
                cb.like(cb.lower(root.get("brand")), likePattern));
    }

    @SafeVarargs
    public static Specification<Product> combine(Specification<Product>... specifications) {
        Specification<Product> result = Specification.where(null);
        for (Specification<Product> spec : specifications) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }
}
