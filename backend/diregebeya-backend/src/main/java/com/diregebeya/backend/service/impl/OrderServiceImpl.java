package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.order.CheckoutRequest;
import com.diregebeya.backend.dto.order.OrderResponse;
import com.diregebeya.backend.dto.order.OrderStatusUpdateRequest;
import com.diregebeya.backend.entity.Cart;
import com.diregebeya.backend.entity.CartItem;
import com.diregebeya.backend.entity.Coupon;
import com.diregebeya.backend.entity.DiscountType;
import com.diregebeya.backend.entity.Order;
import com.diregebeya.backend.entity.OrderItem;
import com.diregebeya.backend.entity.OrderStatus;
import com.diregebeya.backend.entity.PaymentStatus;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.exception.EmptyCartException;
import com.diregebeya.backend.exception.InsufficientStockException;
import com.diregebeya.backend.exception.InvalidCouponException;
import com.diregebeya.backend.exception.InvalidOrderStateException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.OrderMapper;
import com.diregebeya.backend.repository.CartItemRepository;
import com.diregebeya.backend.repository.CartRepository;
import com.diregebeya.backend.repository.CouponRepository;
import com.diregebeya.backend.repository.OrderRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyCartException("Your cart is empty - add items before checking out"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Your cart is empty - add items before checking out");
        }

        Order order = Order.builder()
                .user(userRepository.getReferenceById(userId))
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        // A RuntimeException anywhere in this loop rolls back the whole
        // transaction - including any stock decrements already applied to
        // earlier items in this same pass - so there's no risk of a
        // half-decremented order surviving a later item's stock failure.
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStock()) {
                throw new InsufficientStockException(
                        "Only %d unit(s) of '%s' are in stock".formatted(product.getStock(), product.getName()));
            }
            product.setStock(product.getStock() - cartItem.getQuantity());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            orderItems.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build());
        }

        BigDecimal discount = applyCouponIfPresent(request.getCouponCode(), subtotal, order);

        order.setTotalAmount(subtotal.subtract(discount));
        order.setDiscountAmount(discount);
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        return orderMapper.toResponse(saved);
    }

    /**
     * Validates and redeems a coupon in the same transaction as the rest of
     * checkout - if anything later in checkout fails, the coupon's
     * usedCount increment rolls back too, so a failed checkout never
     * silently burns a redemption. Returns BigDecimal.ZERO (not applying
     * anything) when couponCode is null/blank.
     */
    private BigDecimal applyCouponIfPresent(String couponCode, BigDecimal subtotal, Order order) {
        if (!StringUtils.hasText(couponCode)) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                .orElseThrow(() -> new InvalidCouponException("Coupon code '%s' is not valid".formatted(couponCode)));

        if (!coupon.isActive()) {
            throw new InvalidCouponException("Coupon code '%s' is not active".formatted(couponCode));
        }
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCouponException("Coupon code '%s' has expired".formatted(couponCode));
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new InvalidCouponException("Coupon code '%s' has reached its redemption limit".formatted(couponCode));
        }

        BigDecimal discount = coupon.getDiscountType() == DiscountType.PERCENTAGE
                ? subtotal.multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue().min(subtotal);

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        order.setAppliedCouponCode(coupon.getCode());

        return discount;
    }

    @Override
    public Page<OrderResponse> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = findByIdOrThrow(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("Order", "id", orderId);
        }
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = findByIdOrThrow(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("Order", "id", orderId);
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "This order can no longer be cancelled - it is already %s".formatted(order.getStatus()));
        }

        // Checkout decremented stock per item; cancelling gives it back.
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }
        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = findByIdOrThrow(orderId);
        order.setStatus(request.getStatus());

        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllOrders(OrderStatus statusFilter, Pageable pageable) {
        Page<Order> orders = statusFilter == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(statusFilter, pageable);
        return orders.map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse getAnyOrderById(Long orderId) {
        return orderMapper.toResponse(findByIdOrThrow(orderId));
    }

    private Order findByIdOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", "id", orderId));
    }
}
