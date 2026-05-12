package com.commerce.Online.converter;

import com.commerce.Online.dto.OrderDTO;
import com.commerce.Online.entity.Order;
import com.commerce.Online.entity.OrderItem;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class OrderConverter {

    public OrderDTO toDTO(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .userEmail(order.getUser().getEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems() != null ? order.getItems().stream()
                                                  .map(this::toItemDTO).collect(Collectors.toList()) : null)
                .build();
    }

    private OrderDTO.OrderItemDTO toItemDTO(OrderItem item) {
        return OrderDTO.OrderItemDTO.builder()
                .id(item.getId())
                .articleId(item.getArticle().getId())
                .articleName(item.getArticle().getName())
                .articleImage(item.getArticle().getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}