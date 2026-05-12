package com.commerce.Online.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;

    public BigDecimal getTotal() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        if (items == null) return 0;
        return items.stream().mapToInt(CartItemDTO::getQuantity).sum();
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartItemDTO {
        private Long id;
        private Long articleId;
        private String articleName;
        private String articleImage;
        private String articleBrand;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
