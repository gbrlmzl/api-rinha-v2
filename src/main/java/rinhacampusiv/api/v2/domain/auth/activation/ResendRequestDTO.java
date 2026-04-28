package rinhacampusiv.api.v2.domain.auth.activation;

import jakarta.validation.constraints.NotBlank;

public record ResendRequestDTO(@NotBlank(message = "Nome de usuário obrigatório") String username) {}
