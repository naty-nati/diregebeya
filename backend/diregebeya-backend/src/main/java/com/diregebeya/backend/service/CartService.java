package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.cart.AddCartItemRequest;
import com.diregebeya.backend.dto.cart.CartResponse;
import com.diregebeya.backend.dto.cart.UpdateCartItemRequest;

/**
 * Every method is scoped to a userId - unlike Category/Product, there is no
 * "browse anyone's cart" customer API, only "my cart". The controller
 * supplies userId from the authenticated principal; it's never a path
 * variable a client could tamper with to view someone else's cart.
 */
public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddCartItemRequest request);

    CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long userId, Long itemId);
}
