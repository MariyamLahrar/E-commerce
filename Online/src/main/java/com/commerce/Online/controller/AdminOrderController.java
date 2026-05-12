package com.commerce.Online.controller;

import com.commerce.Online.dto.OrderDTO;
import com.commerce.Online.entity.OrderStatus;
import com.commerce.Online.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // Affiche toutes les commandes (Trié par date décroissante pour voir les nouvelles)
    @GetMapping
    public String listOrders(@RequestParam(defaultValue = "0") int page, Model model) {
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<OrderDTO> orders = orderService.findAllSafe(pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    // Mise à jour du statut (le bouton "OK" de ton formulaire)
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes ra) {
        try {
            orderService.updateStatus(id, status);
            ra.addFlashAttribute("successMsg", "Statut de la commande #" + id + " mis à jour !");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // Suppression d'une commande (le bouton Poubelle)
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("successMsg", "Commande #" + id + " supprimée définitivement.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur lors de la suppression.");
        }
        return "redirect:/admin/orders";
    }
}