package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.validators.constraints.FutureByAtLeastHours;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentCreationData(
        @NotBlank(message = "O nome do torneio é obrigatório")
        @Size(min=10, max=50)
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
        @FutureByAtLeastHours(value = 1, message = "O torneio deve começar em pelo menos 1 hora")
        OffsetDateTime startsAt,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotBlank(message = "É obrigatório que o torneio tenha regras")
        @URL
        String rulesUrl
) {
}
