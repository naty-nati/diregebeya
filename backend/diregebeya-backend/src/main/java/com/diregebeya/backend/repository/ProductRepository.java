package com.diregebeya.backend.repository;

import com.diregebeya.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * {@link JpaSpecificationExecutor} adds findAll(Specification, Pageable) on
 * top of the usual CRUD methods - that's what lets ProductServiceImpl
 * compose an arbitrary combination of optional filters (category, brand,
 * price range, search term) into one query instead of writing a derived
 * query method per combination (which would explode combinatorially).
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Overridden purely to attach an entity graph: without it, ProductMapper
     * reading {@code category} for every row on a listing page fires one
     * extra lazy-load query per product (classic N+1). Only {@code category}
     * is graphed here, not {@code images} - joining a to-many collection
     * with a Pageable makes Hibernate paginate in memory instead of in SQL,
     * which would be worse than the problem this is fixing. Product.images
     * is batch-loaded instead (see {@code @BatchSize} on that field).
     */
    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
