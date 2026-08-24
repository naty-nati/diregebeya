package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.order.CheckoutRequest;
import com.diregebeya.backend.dto.order.OrderResponse;
import com.diregebeya.backend.dto.order.OrderStatusUpdateRequest;
import com.diregebeya.backend.security.UserPrincipal;
import com.diregebeya.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Checkout and order management")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Check out the current cart into a new order, with shipping and payment details and an optional coupon code")
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal UserPrincipal principal,
                                                    @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(principal.getId(), request));
    }

    @Operation(summary = "View the current user's order history")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrderHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderHistory(principal.getId(), pageable));
    }

    @Operation(summary = "Get one of the current user's orders by id")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getId(), id));
    }

    /**
     * PATCH, not PUT: this updates one field (status) on the order, not the
     * whole resource - PUT implies a full replacement of the representation,
     * PATCH is the correct verb for a partial update.
     */
    @Operation(summary = "Update an order's status (admin/staff)")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }
}
