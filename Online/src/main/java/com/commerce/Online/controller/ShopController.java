package com.commerce.Online.controller;

import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ArticleService articleService;
    private final FavoriteService favoriteService;
    private final CartService cartService;

    @GetMapping("/shop")
    public String shop(@RequestParam(defaultValue = "") String search,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "newest") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       Authentication auth,
                       Model model) {

        // 1. Récupération des données communes (Pagination, catégories, etc.)
        Page<ArticleDTO> articles = articleService.searchArticles(search, category, sort, page, 12);
        List<String> categories = articleService.findCategories();

        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());

        // 2. Vérification si l'utilisateur est ADMIN
        boolean isAdmin = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin) {
            // Redirection vers le template de gestion Admin
            // Le fichier doit être dans : templates/admin/shop.html
            return "admin/shop";
        }

        // 3. Logique spécifique pour l'Utilisateur (Favoris et Panier)
        if (auth != null) {
            try {
                // Récupération des IDs des favoris pour afficher les cœurs remplis/vides
                Set<Long> favoriteIds = favoriteService.getFavoriteIds(auth.getName());
                model.addAttribute("favoriteIds", favoriteIds);

                // Nombre d'articles dans le panier pour le badge de la navbar
                int cartCount = cartService.getCartItemCount(auth.getName());
                model.addAttribute("cartCount", cartCount);
            } catch (Exception e) {
                // En cas d'erreur ou si le service n'est pas prêt, on initialise à vide
                model.addAttribute("favoriteIds", new HashSet<>());
            }
        } else {
            // Pour un visiteur non connecté
            model.addAttribute("favoriteIds", new HashSet<>());
        }

        // Retourne le template boutique client classique : templates/shop.html
        return "shop";
    }

    @GetMapping("/shop/article/{id}")
    public String articleDetail(@PathVariable Long id, Authentication auth, Model model) {
        ArticleDTO article = articleService.findById(id);
        model.addAttribute("article", article);

        if (auth != null) {
            // Vérifie si cet article précis est en favori pour l'utilisateur connecté
            boolean isFav = favoriteService.isFavorite(auth.getName(), id);
            model.addAttribute("isFavorite", isFav);
        }

        return "article-detail";
    }
}