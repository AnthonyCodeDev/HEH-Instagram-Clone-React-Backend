package be.heh.stragram.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Le nom d'utilisateur est requis")
        @Size(min = 3, max = 30, message = "Le nom d'utilisateur doit contenir entre 3 et 30 caractères")
        @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Le nom d'utilisateur ne peut contenir que des lettres, des chiffres, des points et des underscores")
        private String username;

        @NotBlank(message = "L'email est requis")
        @Email(message = "L'email doit être valide")
        @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
        private String email;

        @NotBlank(message = "Le mot de passe est requis")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Le nom d'utilisateur ou l'email est requis")
        private String usernameOrEmail;

        @NotBlank(message = "Le mot de passe est requis")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String userId;
        private String username;
    }
}