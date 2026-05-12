package com.commerce.Online.service;

import com.commerce.Online.converter.CartConverter;
import com.commerce.Online.dto.CartDTO;
import com.commerce.Online.entity.*;
import com.commerce.Online.exception.ResourceNotFoundException;
import com.commerce.Online.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CartConverter cartConverter;

    @Transactional(readOnly = true)
    public CartDTO getCartByUsername(String username) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
        return cartConverter.toDTO(cart);
    }

    public CartDTO addItem(String username, Long articleId, int quantity) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable"));

        if (article.getStock() < quantity) {
            throw new IllegalArgumentException("Stock insuffisant. Disponible : " + article.getStock());
        }

        // Vérifier si l'article existe déjà dans le panier
        cartItemRepository.findByCartIdAndArticleId(cart.getId(), articleId)
                .ifPresentOrElse(item -> {
                    int newQty = item.getQuantity() + quantity;
                    if (article.getStock() < newQty) {
                        throw new IllegalArgumentException("Stock insuffisant.");
                    }
                    item.setQuantity(newQty);
                    cartItemRepository.save(item);
                }, () -> {
                    CartItem item = CartItem.builder()
                            .cart(cart)
                            .article(article)
                            .quantity(quantity)
                            .build();
                    cartItemRepository.save(item);
                });

        return cartConverter.toDTO(cartRepository.findById(cart.getId()).orElse(cart));
    }

    public CartDTO updateItem(String username, Long itemId, int quantity) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article du panier introuvable"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Accès refusé");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            if (item.getArticle().getStock() < quantity) {
                throw new IllegalArgumentException("Stock insuffisant.");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return cartConverter.toDTO(cartRepository.findById(cart.getId()).orElse(cart));
    }

    public void removeItem(String username, Long itemId) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article du panier introuvable"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Accès refusé");
        }
        cartItemRepository.delete(item);
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart getCartEntityByUsername(String username) {
        return cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
    }

    @Transactional(readOnly = true)
    public int getCartItemCount(String username) {
        return cartRepository.findByUserUsername(username)
                .map(c -> c.getItems().stream().mapToInt(CartItem::getQuantity).sum())
                .orElse(0);
    }
}