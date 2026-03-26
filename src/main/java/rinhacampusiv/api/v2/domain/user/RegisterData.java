package rinhacampusiv.api.v2.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterData(

        @NotBlank(message = "Username obrigatório")
        @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
        @Pattern(
                regexp = "^[a-z0-9_.]+$",
                message = "Username deve conter apenas letras minúsculas, números, ponto ou _"
        )
        String username,

        @NotBlank(message = "Email obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password

) {}
