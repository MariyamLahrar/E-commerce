package com.commerce.Online.converter;

import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleConverter {

    public ArticleDTO toDTO(Article article) {
        if (article == null) return null;
        return ArticleDTO.builder()
                .id(article.getId())
                .name(article.getName())
                .description(article.getDescription())
                .price(article.getPrice())
                .stock(article.getStock())
                .category(article.getCategory())
                .imageUrl(article.getImageUrl())
                .brand(article.getBrand())
                .active(article.isActive())
                .createdAt(article.getCreatedAt())
                .build();
    }

    public Article toEntity(ArticleDTO dto) {
        if (dto == null) return null;
        return Article.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .brand(dto.getBrand())
                .active(dto.isActive())
                .build();
    }
}