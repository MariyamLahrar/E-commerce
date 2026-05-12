package com.commerce.Online.service;

import com.commerce.Online.converter.ArticleConverter;
import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.entity.Article;
import com.commerce.Online.exception.ResourceNotFoundException;
import com.commerce.Online.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleConverter articleConverter;

    @Transactional(readOnly = true)
    public Page<ArticleDTO> searchArticles(String search, String category, String sort, int page, int size) {
        Sort sorting = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        String searchParam = (search != null && !search.isBlank()) ? search : null;
        String categoryParam = (category != null && !category.isBlank()) ? category : null;
        return articleRepository.searchArticles(searchParam, categoryParam, pageable)
                .map(articleConverter::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDTO> findAllForAdmin(int page, int size) {
        // Note: Assure-toi que le champ s'appelle bien 'id' ou 'createdAt' dans ton entité Article
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return articleRepository.findAll(pageable).map(articleConverter::toDTO);
    }

    // NOUVELLE MÉTHODE CORRIGÉE POUR LE SEARCH ADMIN
    @Transactional(readOnly = true)
    public Page<ArticleDTO> searchForAdmin(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String searchParam = (search != null && !search.isBlank()) ? search : null;

        // On utilise le repository en passant null pour la catégorie (on veut tout)
        return articleRepository.searchArticles(searchParam, null, pageable)
                .map(articleConverter::toDTO);
    }

    @Transactional(readOnly = true)
    public ArticleDTO findById(Long id) {
        return articleConverter.toDTO(articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable : " + id)));
    }

    public Article findEntityById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable : " + id));
    }

    public ArticleDTO create(ArticleDTO dto) {
        Article article = articleConverter.toEntity(dto);
        article.setActive(true);
        return articleConverter.toDTO(articleRepository.save(article));
    }

    public ArticleDTO update(Long id, ArticleDTO dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable : " + id));
        article.setName(dto.getName());
        article.setDescription(dto.getDescription());
        article.setPrice(dto.getPrice());
        article.setStock(dto.getStock());
        article.setCategory(dto.getCategory());
        article.setBrand(dto.getBrand());
        article.setImageUrl(dto.getImageUrl());
        article.setActive(dto.isActive());
        return articleConverter.toDTO(articleRepository.save(article));
    }

    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article introuvable");
        }
        articleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> findCategories() {
        return articleRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public long countArticles() {
        return articleRepository.count();
    }

    private Sort buildSort(String sort) {
        if (sort == null) return Sort.by("id").descending();
        return switch (sort) {
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "name_asc"   -> Sort.by("name").ascending();
            default           -> Sort.by("id").descending();
        };
    }
}