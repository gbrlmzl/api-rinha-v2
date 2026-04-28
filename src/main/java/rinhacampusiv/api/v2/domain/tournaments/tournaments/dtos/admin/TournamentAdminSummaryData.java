package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentAdminSummaryData(
        Long id,
        String slug,
        String name,
        TournamentGame game,
        TournamentStatus status,
        Integer confirmedTeamsCount,
        Integer activeTeamsCount,
        Integer maxTeams,
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String imageUrl
) {

    public TournamentAdminSummaryData(Tournament tournament, Integer confirmedTeamsCount, Integer activeTeamsCount){
        this(
                tournament.getId(),
                tournament.getSlug(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount,
                activeTeamsCount,
                tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getImageUrl()
        );
    }
}
