package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentPublicDetailData(
        Long id,
        String name,
        String description,
        String rulesUrl,
        TournamentGame game,
        TournamentStatus status,
        String teamsRegistered, // Formato "16/32"
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String imageUrl,
        boolean isUserRegistered
) {}