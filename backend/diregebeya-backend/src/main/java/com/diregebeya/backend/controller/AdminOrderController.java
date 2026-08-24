package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.order.OrderResponse;
import com.diregebeya.backend.entity.OrderStatus;
import com.diregebeya.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Deliberately a separate URL space from OrderController's GET /api/orders
 * (which only ever returns "my orders"). The status-update endpoint from
 * Phase 7 (PATCH /api/orders/{id}/status) is already admin-gated there and
 * isn't duplicated here - this controller only adds the visibility Phase 9
 * needs: browsing and inspecting every customer's orders.
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
@Tag(name = "Admin - Orders", description = "Admin visibility across every customer's orders")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "List every order, optionally filtered by status")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(status, pageable));
    }

    @Operation(summary = "Get any order by id, regardless of which customer placed it")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getAnyOrderById(id));
    }
}
