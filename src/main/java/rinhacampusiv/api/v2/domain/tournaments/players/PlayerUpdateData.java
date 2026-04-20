package rinhacampusiv.api.v2.domain.tournaments.players;

import jakarta.validation.constraints.NotNull;

public record PlayerUpdateData(
        @NotNull(message = "ID obrigatório")
        Long id,
        String nickname,
        String discord
) {
}
