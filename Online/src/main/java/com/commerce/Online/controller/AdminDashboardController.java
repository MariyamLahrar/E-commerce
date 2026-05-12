package com.commerce.Online.controller;

import com.commerce.Online.entity.OrderStatus;
import com.commerce.Online.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final ArticleService articleService;
    private final OrderService orderService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {

        // 📊 Statistiques générales
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalArticles", articleService.countArticles());
        model.addAttribute("totalOrders", orderService.countAll());

        model.addAttribute("pendingOrders",
                orderService.countByStatus(OrderStatus.EN_ATTENTE));

        model.addAttribute("validatedOrders",
                orderService.countByStatus(OrderStatus.VALIDEE));

        model.addAttribute("cancelledOrders",
                orderService.countByStatus(OrderStatus.ANNULEE));

        // 💰 Revenue
        BigDecimal revenue = orderService.getTotalRevenue();
        model.addAttribute("totalRevenue", revenue);

        // 📦 Recent orders (CORRIGÉ ✔️)
        model.addAttribute("recentOrders",
                orderService.findAllSafe(OrderStatus.EN_ATTENTE, 0, 5).getContent());

        return "admin/dashboard";
    }
}