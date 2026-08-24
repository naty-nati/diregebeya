package com.diregebeya.backend.dto.admin;

import com.diregebeya.backend.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalProducts;
    private long totalCategories;
    private long totalOrders;
    /** Sum of totalAmount across every order except CANCELLED ones. */
    private BigDecimal totalRevenue;
    private Map<OrderStatus, Long> ordersByStatus;
}
