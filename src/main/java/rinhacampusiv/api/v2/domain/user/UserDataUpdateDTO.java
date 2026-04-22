package rinhacampusiv.api.v2.domain.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// domain/user/UserDataUpdateDTO.java
public record UserDataUpdateDTO(

        @Pattern(
                regexp = "^[\\p{L}\\p{N}_.` ]{3,20}$",
                message = "Nickname deve ter entre 3 e 20 caracteres. Letras (com acento), números, espaço, ponto ou _"
        )
        String nickname,

        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Senha deve conter ao menos uma letra maiúscula e um número")
        String newPassword,

        String currentPassword  // obrigatório se newPassword for enviado


) {}

