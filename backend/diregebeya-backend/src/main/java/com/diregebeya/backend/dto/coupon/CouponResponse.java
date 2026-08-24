package com.diregebeya.backend.dto.coupon;

import com.diregebeya.backend.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer maxUses;
    private int usedCount;
    private Instant expiresAt;
    private boolean active;
}
