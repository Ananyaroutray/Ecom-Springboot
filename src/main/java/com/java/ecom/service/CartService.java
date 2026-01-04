package com.java.ecom.service;

import com.java.ecom.dto.request.AddToCartRequestDto;
import com.java.ecom.dto.request.UpdateCartItemRequestDto;
import com.java.ecom.dto.response.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(Integer userId, AddToCartRequestDto dto);
    CartResponseDto viewCart(Integer userId);
    CartResponseDto updateCartItemQty(Integer userId, Long cartItemId, UpdateCartItemRequestDto dto);
    CartResponseDto removeCartItem(Integer userId, Long cartItemId);
}
