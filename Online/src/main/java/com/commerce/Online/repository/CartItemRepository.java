package com.commerce.Online.repository;

import com.commerce.Online.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndArticleId(Long cartId, Long articleId);
    void deleteByCartId(Long cartId);
}
