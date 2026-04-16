package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentUpdateData(
        String name,
        TournamentGame game,
        Integer maxTeams,
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        String description,
        String imageUrl,
        TournamentStatus status,
        String rulesUrl
) {
}
