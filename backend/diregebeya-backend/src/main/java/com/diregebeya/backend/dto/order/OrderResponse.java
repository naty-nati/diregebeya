package com.diregebeya.backend.dto.order;

import com.diregebeya.backend.entity.OrderStatus;
import com.diregebeya.backend.entity.PaymentMethod;
import com.diregebeya.backend.entity.PaymentStatus;
import com.diregebeya.backend.entity.ShippingAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String appliedCouponCode;
    private ShippingAddress shippingAddress;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
}
