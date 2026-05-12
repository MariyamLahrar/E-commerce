package com.commerce.Online.converter;

import com.commerce.Online.dto.CartDTO;
import com.commerce.Online.entity.Cart;
import com.commerce.Online.entity.CartItem;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class CartConverter {

    public CartDTO toDTO(Cart cart) {
        if (cart == null) return null;
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(cart.getItems() != null ? cart.getItems().stream()
                                                 .map(this::toItemDTO).collect(Collectors.toList()) : null)
                .build();
    }

    private CartDTO.CartItemDTO toItemDTO(CartItem item) {
        return CartDTO.CartItemDTO.builder()
                .id(item.getId())
                .articleId(item.getArticle().getId())
                .articleName(item.getArticle().getName())
                .articleImage(item.getArticle().getImageUrl())
                .articleBrand(item.getArticle().getBrand())
                .unitPrice(item.getArticle().getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}