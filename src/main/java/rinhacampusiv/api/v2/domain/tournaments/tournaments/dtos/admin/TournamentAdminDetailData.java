package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentAdminDetailData(
        Long id,
        String name,
        TournamentGame game,
        TournamentStatus status,
        Integer maxTeams,
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        OffsetDateTime createdAt,
        String description,
        String imageUrl,
        String rulesUrl
) {
}
