package com.commerce.Online.controller;

import com.commerce.Online.dto.ArticleDTO;
import com.commerce.Online.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/articles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @GetMapping
    public String listArticles(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "") String search,
                               Model model) {
        Page<ArticleDTO> articles;

        // CORRECTION : On vérifie si une recherche est en cours
        if (search != null && !search.trim().isEmpty()) {
            // Assurez-vous d'avoir créé cette méthode dans votre ArticleService
            articles = articleService.searchForAdmin(search, page, 10);
        } else {
            articles = articleService.findAllForAdmin(page, 10);
        }

        model.addAttribute("articles", articles);
        model.addAttribute("search", search);
        return "admin/articles";
    }

    @GetMapping("/new")
    public String newArticleForm(Model model) {
        model.addAttribute("articleDTO", new ArticleDTO());
        model.addAttribute("categories", articleService.findCategories());
        return "admin/article-form";
    }

    @PostMapping("/new")
    public String createArticle(@Valid @ModelAttribute("articleDTO") ArticleDTO dto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", articleService.findCategories());
            return "admin/article-form";
        }
        articleService.create(dto);
        redirectAttributes.addFlashAttribute("successMsg",
                " Article créé avec succès !");
        return "redirect:/admin/articles";
    }

    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        model.addAttribute("articleDTO", articleService.findById(id));
        model.addAttribute("categories", articleService.findCategories());
        return "admin/article-form";
    }

    @PostMapping("/edit/{id}")
    public String updateArticle(@PathVariable Long id,
                                @Valid @ModelAttribute("articleDTO") ArticleDTO dto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", articleService.findCategories());
            return "admin/article-form";
        }
        articleService.update(id, dto);
        redirectAttributes.addFlashAttribute("successMsg",
                " Article modifié avec succès !");
        return "redirect:/admin/articles";
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            articleService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg",
                    "🗑 Article supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/articles";
    }
}