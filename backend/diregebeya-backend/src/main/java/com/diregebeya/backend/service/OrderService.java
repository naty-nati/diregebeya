package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.order.CheckoutRequest;
import com.diregebeya.backend.dto.order.OrderResponse;
import com.diregebeya.backend.dto.order.OrderStatusUpdateRequest;
import com.diregebeya.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /**
     * Converts the caller's current cart into a placed order and empties
     * the cart. {@code request} carries the shipping address, payment
     * method, and an optional coupon code (null/blank = no coupon); when
     * present the coupon must be an active, unexpired, not-fully-redeemed
     * code, validated and redeemed atomically as part of this same checkout
     * transaction - see OrderServiceImpl.applyCouponIfPresent.
     */
    OrderResponse checkout(Long userId, CheckoutRequest request);

    Page<OrderResponse> getOrderHistory(Long userId, Pageable pageable);

    /** Ownership-checked: only the customer who placed the order (not just any authenticated user) can view it. */
    OrderResponse getOrderById(Long userId, Long orderId);

    /**
     * Ownership-checked, and only allowed while the order is still PENDING -
     * once an admin has moved it to CONFIRMED or later, the customer can no
     * longer self-serve a cancellation. Restores each item's stock.
     */
    OrderResponse cancelOrder(Long userId, Long orderId);

    /** Admin only - no ownership check, since an admin manages every customer's orders. */
    OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request);

    /** Admin only - every order across every customer, optionally filtered by status. */
    Page<OrderResponse> getAllOrders(OrderStatus statusFilter, Pageable pageable);

    /** Admin only - unlike getOrderById, not restricted to the order's own customer. */
    OrderResponse getAnyOrderById(Long orderId);
}
