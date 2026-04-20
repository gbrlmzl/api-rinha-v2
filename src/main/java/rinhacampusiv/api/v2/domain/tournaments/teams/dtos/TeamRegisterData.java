package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PlayerRegisterData;

import java.util.List;

public record TeamRegisterData(
        @NotBlank(message = "O campo nome é obrigatório")
        String teamName,

        @NotNull(message = "O envio dos dados dos jogadores é obrigatório")
        @Size(min = 5, max = 6, message = "Mínimo de jogadores: 5, Máximo de jogadores: 6")
        @Valid
        List<PlayerRegisterData> players



) {
}
