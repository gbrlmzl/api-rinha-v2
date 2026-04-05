package rinhacampusiv.api.v2.domain.passwordReset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// ─── Request: definir nova senha ───────────────────────────────────────────
public record NewPasswordRequest(
        @NotBlank(message = "Token obrigatório")
        String token,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Senha deve conter ao menos uma letra maiúscula e um número")
        String newPassword
) {}