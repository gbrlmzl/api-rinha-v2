package rinhacampusiv.api.v2.domain.tournaments.teams;

import jakarta.validation.constraints.NotNull;

import java.io.File;

public record TeamUpdateData(
        @NotNull(message = "ID obrigatorio")
        Long id,

        File shieldFile,

        String shieldUrl,

        String teamName
) {
}
