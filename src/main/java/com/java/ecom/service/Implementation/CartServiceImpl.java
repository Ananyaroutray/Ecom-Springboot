package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.AddToCartRequestDto;
import com.java.ecom.dto.request.UpdateCartItemRequestDto;
import com.java.ecom.dto.response.CartItemResponseDto;
import com.java.ecom.dto.response.CartResponseDto;
import com.java.ecom.entity.Cart;
import com.java.ecom.entity.CartItem;
import com.java.ecom.entity.Product;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.repository.CartRepo;
import com.java.ecom.repository.ProductRepo;
import com.java.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    @Override
    @Transactional
    public CartResponseDto addToCart(Integer userId, AddToCartRequestDto dto) {
        // 1️ Fetch product
        Product product = productRepo.findById(dto.getProductId()).orElseThrow(()->new NotFoundException("product not found"));

        // 2️ Validate product
        if(!Boolean.TRUE.equals(product.getIsActive()) || !Boolean.TRUE.equals(product.getIsAvailable())){
            throw new RuntimeException("Product is not available");
        }
        // 3️ Validate stock
        if(dto.getQuantity()>product.getStock()){
            throw new RuntimeException("Insufficient stock");
        }
        //4️ Fetch or create cart
        Cart cart = cartRepo.findByUserId(userId).orElseGet(()->createNewCart(userId));

        // 5️ Check if product already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();
        if(existingItem.isPresent()){
            //update quantity
            CartItem item = existingItem.get();
            int newQty = item.getQuantity()+ dto.getQuantity();

            if(newQty>product.getStock()){
                throw new RuntimeException("Stock exceeded");
            }
            item.setQuantity(newQty);
            item.setSubTotal(item.getPrice()*newQty);
        }else{
            // Add new item
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setProductName(product.getProdName());
            item.setPrice(product.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setSubTotal(product.getPrice() * dto.getQuantity());

            cart.getItems().add(item);
        }
        // 6️ Recalculate cart total
        recalculateCartTotal(cart);
        // 7️ Save cart
        Cart savedCart = cartRepo.save(cart);
        // 8️ Map to response
        return mapToResponse(savedCart);
    }

    @Override
    public CartResponseDto viewCart(Integer userId) {
        Cart cart = cartRepo.findByUserId(userId).orElseGet(()-> createNewCart(userId));
        return mapToResponse(cart);
    }

    @Override
    public CartResponseDto updateCartItemQty(Integer userId, Long cartItemId, UpdateCartItemRequestDto dto) {

        // 1️ Fetch cart
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(()-> new NotFoundException("UserId not found"));

        // 2️ Find cart item
        CartItem item = cart.getItems().stream()
                .filter(i->i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(()->new NotFoundException("Cart Item not found"));

        // 3️ Validate quantity
        if(dto.getQuantity()<1){
            throw new RuntimeException("Quantity must be at least 1");
        }

        //4 validate stock
        Product product = productRepo.findById(item.getProductId()).orElseThrow(()->new NotFoundException("Product not found"));
        if(dto.getQuantity()> product.getStock()){
            throw new RuntimeException("Insufficient stock");
        }

        // 5️ Update item
        item.setQuantity(dto.getQuantity());
        item.setSubTotal(item.getSubTotal() * dto.getQuantity());

        // 6️ Recalculate cart total
        recalculateCartTotal(cart);

        // 7️ Save & return
        Cart savedCart = cartRepo.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDto removeCartItem(Integer userId, Long cartItemId) {

        // 1️ Fetch cart
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2️ Find cart item
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // 3️ Remove item
        cart.getItems().remove(item); // orphanRemoval = true

        // 4️ Recalculate total
        recalculateCartTotal(cart);

        // 5️ Save & return
        Cart savedCart = cartRepo.save(cart);
        return mapToResponse(savedCart);
    }


    private Cart createNewCart(Integer userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalAmount(0.0);
        return cart;
    }

    private void recalculateCartTotal(Cart cart) {
        double total = cart.getItems()
                .stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        cart.setTotalAmount(total);
    }

    private CartResponseDto mapToResponse(Cart cart) {

        List<CartItemResponseDto> items = cart.getItems()
                .stream()
                .map(item -> {
                    CartItemResponseDto dto = new CartItemResponseDto();
                    dto.setId(item.getId());
                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    dto.setSubTotal(item.getSubTotal());
                    return dto;
                })
                .toList();

        CartResponseDto response = new CartResponseDto();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalAmount(cart.getTotalAmount());
        response.setItems(items);

        return response;
    }

}
