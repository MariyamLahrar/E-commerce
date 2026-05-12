package com.commerce.Online.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ArticleDTO {
    private Long id;

    @NotBlank(message = "Le nom est requis")
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Le prix est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal price;

    @NotNull(message = "Le stock est requis")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock;

    private String category;
    private String imageUrl;
    private String brand;
    private boolean active;
    private LocalDateTime createdAt;
}