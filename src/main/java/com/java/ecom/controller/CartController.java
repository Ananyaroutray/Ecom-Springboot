package com.java.ecom.controller;


import com.java.ecom.dto.request.AddToCartRequestDto;
import com.java.ecom.dto.request.UpdateCartItemRequestDto;
import com.java.ecom.dto.response.CartResponseDto;
import com.java.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add product to cart
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponseDto> addToCart(@PathVariable Integer userId, @RequestBody AddToCartRequestDto dto){
        return ResponseEntity.ok(cartService.addToCart(userId,dto));
    }

    //view cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> viewCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.viewCart(userId));
    }

    //update cart
    @PutMapping("/{userId}/item/{itemId}")
    public ResponseEntity<CartResponseDto> updateCartItemQty(@PathVariable Integer userId,
                                                             @PathVariable Long itemId,
                                                             @RequestBody UpdateCartItemRequestDto dto){
        return ResponseEntity.ok(
                cartService.updateCartItemQty(userId, itemId, dto)
        );
    }

    //delete cart item
    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<CartResponseDto> removeCartItem(
            @PathVariable Integer userId,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                cartService.removeCartItem(userId, itemId)
        );
    }


}
