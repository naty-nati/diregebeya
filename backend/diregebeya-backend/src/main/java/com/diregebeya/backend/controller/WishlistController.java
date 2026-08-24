package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.wishlist.WishlistItemResponse;
import com.diregebeya.backend.security.UserPrincipal;
import com.diregebeya.backend.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Scoped to the authenticated user exactly like CartController - no admin
 * surface, no ownership parameter to tamper with.
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "The authenticated customer's saved products")
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "View the current user's wishlist")
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(wishlistService.getWishlist(principal.getId()));
    }

    @Operation(summary = "Add a product to the wishlist (idempotent)")
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> add(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(principal.getId(), productId));
    }

    /**
     * DELETE is naturally idempotent - removing an already-absent product
     * still returns 204 rather than 404, matching the HTTP spec's intent
     * for this verb (the end state the caller wants - "not wishlisted" -
     * is already true).
     */
    @Operation(summary = "Remove a product from the wishlist")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long productId) {
        wishlistService.removeFromWishlist(principal.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}
