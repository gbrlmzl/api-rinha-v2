package rinhacampusiv.api.v2.domain.passwordReset;

import jakarta.validation.constraints.NotBlank;


// ─── Request: solicitar recuperação de senha ───────────────────────────────
public record PasswordResetRequest(
        @NotBlank(message = "Username obrigatório")
        String username
) {}