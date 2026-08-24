package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.wishlist.WishlistItemResponse;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.entity.Wishlist;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.WishlistMapper;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.repository.WishlistRepository;
import com.diregebeya.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(wishlistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {
        Wishlist existing = wishlistRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (existing != null) {
            return wishlistMapper.toResponse(existing);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", "id", productId));

        Wishlist wishlist = Wishlist.builder()
                .user(userRepository.getReferenceById(userId))
                .product(product)
                .build();

        return wishlistMapper.toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(wishlistRepository::delete);
    }
}
