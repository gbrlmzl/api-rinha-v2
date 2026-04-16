package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import jakarta.validation.constraints.*;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentCreationData(
        @NotBlank(message = "O nome do torneio é obrigatório")
        String name,

        @NotNull(message = "O tipo de jogo é obrigatório")
        TournamentGame game,

        @NotNull(message = "O limite de equipes é obrigatório")
        @Min(value = 2, message = "O torneio deve ter no mínimo 2 equipes")
        Integer maxTeams,

        @NotNull(message = "A premiação é obrigatória")
        @PositiveOrZero(message = "A premiação não pode ser negativa")
        BigDecimal prizePool,

        @NotNull(message = "A data de início é obrigatória")
        @Future(message = "A data de início deve ser no futuro")
        OffsetDateTime startsAt,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotBlank(message = "A URL da imagem é obrigatória")
        String imageUrl,

        @NotBlank(message = "É obrigatório que o torneio tenha regras")
        String rulesUrl
) {
}
