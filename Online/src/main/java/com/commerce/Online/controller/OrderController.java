package com.commerce.Online.controller;

import com.commerce.Online.dto.OrderDTO;
import com.commerce.Online.service.CartService;
import com.commerce.Online.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping
    public String myOrders(@RequestParam(defaultValue = "0") int page,
                           Authentication auth,
                           Model model) {
        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        Page<OrderDTO> ordersPage = orderService.findByUsername(username, page, 10);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("cartCount", cartService.getCartItemCount(username));

        return "orders";
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetails(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes ra) {
        try {
            OrderDTO order = orderService.findById(id);
            if (!order.getUsername().equals(auth.getName())) {
                return "redirect:/orders";
            }
            model.addAttribute("order", order);
            return "order-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Détails indisponibles.");
            return "redirect:/orders";
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            orderService.cancelOrder(id, auth.getName());
            ra.addFlashAttribute("successMsg", "Commande annulée.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/orders";
    }
}