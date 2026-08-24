package com.diregebeya.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Money is modeled as {@link BigDecimal}, never double/float - binary
 * floating point can't represent most decimal fractions exactly (0.1 + 0.2
 * != 0.3), which is unacceptable once you're summing prices into an order
 * total. {@code precision = 10, scale = 2} caps it at 99,999,999.99, plenty
 * for retail prices.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    /**
     * LAZY (unlike User.roles) because product listings are read far more
     * often than the category is actually needed, and a category has real
     * weight (name + description) worth not fetching on every row when
     * paging through hundreds of products. ProductMapper triggers the fetch
     * only when building the response, inside the same transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * {@code mappedBy} marks this as the inverse side - ProductImage owns
     * the foreign key, so this collection is purely a convenience view over
     * it. {@code orphanRemoval = true} means removing an image from this
     * list (not just deleting it directly) deletes its row, which matches
     * how ProductImageServiceImpl manages the gallery.
     */
    /**
     * BatchSize means Hibernate loads this collection for up to 32 products
     * in a single "WHERE product_id IN (...)" query instead of one query per
     * product when a page of products is iterated - the other half of the
     * N+1 fix alongside ProductRepository's category entity graph.
     */
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @BatchSize(size = 32)
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
