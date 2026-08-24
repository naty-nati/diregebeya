package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.wishlist.WishlistItemResponse;

import java.util.List;

public interface WishlistService {

    List<WishlistItemResponse> getWishlist(Long userId);

    /** Idempotent - adding an already-wishlisted product returns the existing entry rather than erroring. */
    WishlistItemResponse addToWishlist(Long userId, Long productId);

    void removeFromWishlist(Long userId, Long productId);
}
