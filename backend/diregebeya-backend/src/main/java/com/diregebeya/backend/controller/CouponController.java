package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.coupon.CouponRequest;
import com.diregebeya.backend.dto.coupon.CouponResponse;
import com.diregebeya.backend.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin-only end to end, unlike Category/Product - there is no
 * customer-facing "browse coupons" list (that would hand out every promo
 * code to anyone who asks). Customers only ever interact with a coupon
 * indirectly, by passing a code they already know to checkout
 * (POST /api/orders/checkout?couponCode=...), which validates and redeems
 * it atomically rather than through a separate preview/apply step.
 */
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Coupons", description = "Admin management of discount coupons")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "List all coupons")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAll() {
        return ResponseEntity.ok(couponService.getAll());
    }

    @Operation(summary = "Create a coupon")
    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.create(request));
    }

    @Operation(summary = "Update a coupon")
    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> update(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.update(id, request));
    }

    @Operation(summary = "Delete a coupon")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
