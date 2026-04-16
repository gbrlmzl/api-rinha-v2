package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentPublicSummaryData(
        Long id,
        String name,
        TournamentGame game,
        TournamentStatus status,
        String teamsRegistered, // Formato "16/32"
        OffsetDateTime startsAt,
        BigDecimal prizePool,
        String imageUrl,
        String rulesUrl
) {
}
