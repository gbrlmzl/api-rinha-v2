package rinhacampusiv.api.v2.domain.activation;

import jakarta.validation.constraints.NotBlank;

public record ActivateRequestDTO(@NotBlank(message = "Token obrigatório") String token){}