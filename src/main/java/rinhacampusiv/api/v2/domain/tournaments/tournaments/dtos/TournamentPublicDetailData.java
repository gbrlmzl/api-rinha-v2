package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
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
        Integer confirmedTeamsCount,
        Integer maxTeams,
        BigDecimal prizePool,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String imageUrl
) {
    public TournamentPublicDetailData(Tournament tournament, Integer confirmedTeamsCount){
        this(
                tournament.getId(),
                tournament.getName(),
                tournament.getDescription(),
                tournament.getRulesUrl(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount,
                tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getImageUrl()
        );
    }
}
