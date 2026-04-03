package rinhacampusiv.api.v2.domain.tournaments.registrations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentRegistrationDataMercadoPago(
        @NotBlank(message = "O campo nome é obrigatório")
        String nome,

        @NotBlank(message = "O campo sobrenome é obrigatório")
        String sobrenome,

        @Email(message = "Email inválido")
        @NotBlank(message = "O campo email é obrigatório")
        String email,

        @NotBlank(message = "O campo cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF inválido")
        String cpf
) {
}
