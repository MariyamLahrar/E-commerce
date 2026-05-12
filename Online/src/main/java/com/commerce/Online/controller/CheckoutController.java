package com.commerce.Online.controller;

import com.commerce.Online.dto.CartDTO;
import com.commerce.Online.service.CartService;
import com.commerce.Online.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping
    public String checkoutPage(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        CartDTO cart = cartService.getCartByUsername(auth.getName());

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("title", "Paiement");

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(Authentication auth, RedirectAttributes ra) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            orderService.placeOrder(auth.getName());
            ra.addFlashAttribute("successMsg", "Commande passée avec succès !");
            return "redirect:/orders";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
            return "redirect:/cart";
        }
    }
}