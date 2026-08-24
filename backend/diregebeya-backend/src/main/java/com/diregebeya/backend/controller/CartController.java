package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.cart.AddCartItemRequest;
import com.diregebeya.backend.dto.cart.CartResponse;
import com.diregebeya.backend.dto.cart.UpdateCartItemRequest;
import com.diregebeya.backend.security.UserPrincipal;
import com.diregebeya.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * No admin endpoints here and no @PreAuthorize - every method only needs
 * "authenticated" (already the SecurityConfig default), because the userId
 * always comes from the token, never a path/query parameter. There is no
 * URL a customer could edit to reach another customer's cart.
 *
 * Every mutation returns the resulting CartResponse rather than 201/204 -
 * a cart client typically re-renders the whole cart after any change, so
 * returning it directly saves a follow-up GET /api/cart round trip.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "The authenticated customer's shopping cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "View the current user's cart")
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getId()));
    }

    @Operation(summary = "Add a product to the cart (increments quantity if already present)")
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal UserPrincipal principal,
                                                  @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getId(), request));
    }

    @Operation(summary = "Update a cart item's quantity")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(@AuthenticationPrincipal UserPrincipal principal,
                                                             @PathVariable Long itemId,
                                                             @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(principal.getId(), itemId, request));
    }

    @Operation(summary = "Remove an item from the cart")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(principal.getId(), itemId));
    }
}
