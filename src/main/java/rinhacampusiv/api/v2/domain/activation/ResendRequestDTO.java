package rinhacampusiv.api.v2.domain.activation;

import jakarta.validation.constraints.NotBlank;

public record ResendRequestDTO(@NotBlank(message = "Nome de usuário obrigatório") String username) {}
