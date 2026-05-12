package com.commerce.Online.service;

import com.commerce.Online.converter.ArticleConverter;
import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.entity.*;
import com.commerce.Online.exception.ResourceNotFoundException;
import com.commerce.Online.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleConverter articleConverter;

    public void addFavorite(String username, Long articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable"));
        user.getFavorites().add(article);
        userRepository.save(user);
    }

    public void removeFavorite(String username, Long articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.getFavorites().removeIf(a -> a.getId().equals(articleId));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return user.getFavorites().stream()
                .map(articleConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Set<Long> getFavoriteIds(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return user.getFavorites().stream().map(Article::getId).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String username, Long articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return user.getFavorites().stream().anyMatch(a -> a.getId().equals(articleId));
    }
}