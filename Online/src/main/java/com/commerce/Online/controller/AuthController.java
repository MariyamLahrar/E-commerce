package com.commerce.Online.controller;

import com.commerce.Online.dto.UserDTO;
import com.commerce.Online.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Affiche la page de connexion.
     * Si l'utilisateur est déjà connecté, on le redirige vers la boutique.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Authentication authentication,
                            Model model) {

        // Redirection si déjà authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/shop";
        }

        if (error != null) {
            model.addAttribute("errorMsg", "Identifiants incorrects. Veuillez réessayer.");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Vous avez été déconnecté avec succès.");
        }

        model.addAttribute("title", "Connexion");
        return "login";
    }

    /**
     * Affiche la page d'inscription.
     */
    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        // Redirection si déjà authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/shop";
        }

        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("title", "Créer un compte");
        return "register";
    }

    /**
     * Traite le formulaire d'inscription.
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDTO") UserDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        // Vérification des erreurs de validation (champs vides, format email, etc.)
        if (result.hasErrors()) {
            model.addAttribute("title", "Créer un compte");
            return "register";
        }

        try {
            userService.register(dto);
            // On utilise FlashAttribute pour que le message survive à la redirection
            redirectAttributes.addFlashAttribute("successMsg", "Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Cas où l'email ou le username existe déjà (logique gérée dans le service)
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("title", "Créer un compte");
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Une erreur inattendue est survenue lors de l'inscription.");
            return "register";
        }
    }

    /**
     * Redirection de la racine vers la boutique.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/shop";
    }
}