package com.commerce.Online.controller;

import com.commerce.Online.dto.CartDTO;
import com.commerce.Online.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        // 1. Vérification stricte
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Récupération sécurisée
        CartDTO cart = cartService.getCartByUsername(auth.getName());

        // 3. Empêcher Thymeleaf de crasher si le panier est vide
        if (cart == null) {
            cart = CartDTO.builder()
                    .items(new ArrayList<>())
                    .build();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("title", "Mon Panier");
        return "cart";
    }

    @PostMapping("/add")
    public String addItem(@RequestParam Long articleId,
                          @RequestParam(defaultValue = "1") int quantity,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {

        if (auth == null) return "redirect:/login";

        try {
            cartService.addItem(auth.getName(), articleId, quantity);
            redirectAttributes.addFlashAttribute("successMsg", "Article ajouté au panier !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }

        return "redirect:/shop";
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam Long articleId,
                             @RequestParam int quantity,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {

        if (auth == null) return "redirect:/login";

        try {
            cartService.updateItem(auth.getName(), articleId, quantity);
            redirectAttributes.addFlashAttribute("successMsg", "Quantité mise à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erreur de mise à jour.");
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeItem(@PathVariable Long itemId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {

        if (auth == null) return "redirect:/login";

        try {
            cartService.removeItem(auth.getName(), itemId);
            redirectAttributes.addFlashAttribute("successMsg", "Article supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Impossible de supprimer l'article.");
        }

        return "redirect:/cart";
    }
}