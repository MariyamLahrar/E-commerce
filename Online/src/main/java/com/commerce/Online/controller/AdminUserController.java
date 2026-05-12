package com.commerce.Online.controller;

import com.commerce.Online.dto.UserDTO;
import com.commerce.Online.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "") String search,
                            Model model) {
        Page<UserDTO> users = userService.findAll(search, page, 10);
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @PostMapping("/new")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(required = false) String firstName,
                             @RequestParam(required = false) String lastName,
                             @RequestParam(required = false) String phone,
                             @RequestParam(defaultValue = "ROLE_USER") String role,
                             @RequestParam(defaultValue = "true") boolean enabled,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDTO dto = new UserDTO();
            dto.setUsername(username);
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setPhone(phone);
            dto.setEnabled(enabled);
            dto.setRoles(Set.of(role));
            userService.createUserByAdmin(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    " Utilisateur créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String firstName,
                             @RequestParam(required = false) String lastName,
                             @RequestParam(required = false) String phone,
                             @RequestParam(defaultValue = "true") boolean enabled,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDTO existing = userService.findById(id);
            existing.setUsername(username);
            existing.setEmail(email);
            existing.setFirstName(firstName);
            existing.setLastName(lastName);
            existing.setPhone(phone);
            existing.setEnabled(enabled);
            if (password != null && !password.isBlank()) {
                existing.setPassword(password);
            }
            userService.updateUser(id, existing);
            redirectAttributes.addFlashAttribute("successMsg",
                    " Utilisateur modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMsg",
                    "🗑 Utilisateur supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}