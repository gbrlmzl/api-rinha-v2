package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentAdminSummaryData(
        Long id,
        String name,
        TournamentGame game,
        TournamentStatus status,
        String teams, // Formato "16/32"
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String imageUrl
) {
}
