package com.commerce.Online.controller;

import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.service.CartService;
import com.commerce.Online.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final CartService cartService;

    @GetMapping
    public String favorites(Authentication auth, Model model) {
        List<ArticleDTO> favorites = favoriteService.getFavorites(auth.getName());
        model.addAttribute("favorites", favorites);
        model.addAttribute("cartCount",
                cartService.getCartItemCount(auth.getName()));
        return "favorites";
    }

    @PostMapping("/add/{articleId}")
    public String addFavorite(@PathVariable Long articleId,
                              @RequestParam(defaultValue = "/shop") String redirect,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            favoriteService.addFavorite(auth.getName(), articleId);
            redirectAttributes.addFlashAttribute("successMsg",
                    " Ajouté aux favoris !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:" + redirect;
    }

    @PostMapping("/remove/{articleId}")
    public String removeFavorite(@PathVariable Long articleId,
                                 @RequestParam(defaultValue = "/favorites") String redirect,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            favoriteService.removeFavorite(auth.getName(), articleId);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Retiré des favoris.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:" + redirect;
    }
}