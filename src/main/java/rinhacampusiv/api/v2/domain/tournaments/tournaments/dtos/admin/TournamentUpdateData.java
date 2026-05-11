package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentUpdateData(
        @Size(min=10, max=50)
        String name,
        TournamentGame game,
        Integer maxTeams,
        @PositiveOrZero
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String description,
        String imageUrl,
        TournamentStatus status,
        String rulesUrl
) {
}
