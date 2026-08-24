package com.diregebeya.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * {@code usedCount} is incremented, not decremented - redemption is one-way.
 * {@code maxUses null} means unlimited; {@code expiresAt null} means it
 * never expires. Concurrency note: incrementing usedCount and checking it
 * against maxUses in the same transaction has the same race-condition
 * exposure as Product.stock in checkout (Phase 7) - two simultaneous
 * checkouts on the very last allowed use could both pass validation before
 * either commits. Same known limitation, not solved here for the same
 * reason: it needs optimistic locking or a conditional UPDATE, which is a
 * deliberate follow-up rather than in-scope now.
 */
@Entity
@Table(name = "coupons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    /** Null means unlimited redemptions. */
    private Integer maxUses;

    @Builder.Default
    @Column(nullable = false)
    private int usedCount = 0;

    /** Null means it never expires. */
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
