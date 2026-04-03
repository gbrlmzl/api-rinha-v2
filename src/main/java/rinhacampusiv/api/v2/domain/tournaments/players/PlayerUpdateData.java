package rinhacampusiv.api.v2.domain.tournaments.players;

import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.user.User;

public record PlayerUpdateData(
        @NotNull(message = "ID obrigatório")
        Long id,
        String nickname,
        String discord
) {
}
