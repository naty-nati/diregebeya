package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.cart.AddCartItemRequest;
import com.diregebeya.backend.dto.cart.CartItemResponse;
import com.diregebeya.backend.dto.cart.CartResponse;
import com.diregebeya.backend.dto.cart.UpdateCartItemRequest;
import com.diregebeya.backend.entity.Cart;
import com.diregebeya.backend.entity.CartItem;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.exception.InsufficientStockException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.CartItemMapper;
import com.diregebeya.backend.repository.CartItemRepository;
import com.diregebeya.backend.repository.CartRepository;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.repository.UserRepository;
import com.diregebeya.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartResponse getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> buildCartResponse(cart.getId()))
                .orElseGet(this::emptyCartResponse);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ResourceNotFoundException.of("Product", "id", request.getProductId()));

        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        int newQuantity = (item == null ? 0 : item.getQuantity()) + request.getQuantity();
        ensureStockAvailable(product, newQuantity);

        if (item == null) {
            item = CartItem.builder().cart(cart).product(product).quantity(request.getQuantity()).build();
        } else {
            item.setQuantity(newQuantity);
        }
        cartItemRepository.save(item);

        return buildCartResponse(cart.getId());
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem item = findOwnedItemOrThrow(userId, itemId);
        ensureStockAvailable(item.getProduct(), request.getQuantity());
        item.setQuantity(request.getQuantity());

        return buildCartResponse(item.getCart().getId());
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        CartItem item = findOwnedItemOrThrow(userId, itemId);
        Long cartId = item.getCart().getId();
        cartItemRepository.delete(item);

        return buildCartResponse(cartId);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(userRepository.getReferenceById(userId)).build()));
    }

    /**
     * Ownership is enforced by comparing the item's cart's user, not by
     * scoping the query itself - this way a mismatch and a missing id look
     * identical to the caller (404 either way), so a customer can't probe
     * which item ids belong to someone else's cart.
     */
    private CartItem findOwnedItemOrThrow(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.of("CartItem", "id", itemId));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("CartItem", "id", itemId);
        }
        return item;
    }

    private void ensureStockAvailable(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Only %d unit(s) of '%s' are in stock".formatted(product.getStock(), product.getName()));
        }
    }

    private CartResponse buildCartResponse(Long cartId) {
        List<CartItemResponse> items = cartItemRepository.findByCartId(cartId).stream()
                .map(cartItemMapper::toResponse)
                .toList();

        int totalItems = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder().items(items).totalItems(totalItems).totalPrice(totalPrice).build();
    }

    private CartResponse emptyCartResponse() {
        return CartResponse.builder().items(List.of()).totalItems(0).totalPrice(BigDecimal.ZERO).build();
    }
}
