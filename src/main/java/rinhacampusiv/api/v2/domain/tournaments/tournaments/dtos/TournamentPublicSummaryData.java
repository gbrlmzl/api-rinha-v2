package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentPublicSummaryData(
        Long id,
        String slug,
        String name,
        TournamentGame game,
        TournamentStatus status,
        Integer confirmedTeamsCount,
        Integer maxTeams,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        BigDecimal prizePool,
        String imageUrl,
        String rulesUrl
) {
    public TournamentPublicSummaryData(Tournament tournament, int confirmedTeamsCount){
        this(
                tournament.getId(),
                tournament.getSlug(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount,
                tournament.getMaxTeams(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getPrizePool(),
                tournament.getImageUrl(),
                tournament.getRulesUrl()
        );
    }
}
