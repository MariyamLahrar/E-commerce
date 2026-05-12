package com.commerce.Online.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private String phone;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Set<String> roles;
    private int orderCount;
}