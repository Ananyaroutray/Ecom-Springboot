package com.java.ecom.service;

import com.java.ecom.dto.request.AddToCartRequestDto;
import com.java.ecom.dto.request.UpdateCartItemRequestDto;
import com.java.ecom.dto.response.CartResponseDto;

import java.util.UUID;

public interface CartService {
    CartResponseDto addToCart(UUID userId, AddToCartRequestDto dto);
    CartResponseDto viewCart(UUID userId);
    CartResponseDto updateCartItemQty(UUID userId, Long cartItemId, UpdateCartItemRequestDto dto);
    CartResponseDto removeCartItem(UUID userId, Long cartItemId);
}
