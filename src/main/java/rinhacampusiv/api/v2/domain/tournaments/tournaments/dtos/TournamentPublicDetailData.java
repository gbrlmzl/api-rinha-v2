package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;



import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamPublicData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.UserTeamStatusData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TournamentPublicDetailData(
        Long id,
        String slug,
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
        String imageUrl,
        List<TeamPublicData> confirmedTeams,
        UserTeamStatusData userTeam
) {
    public TournamentPublicDetailData(Tournament tournament, Integer confirmedTeamsCount, List<TeamPublicData> confirmedTeams, UserTeamStatusData userTeam){
        this(
                tournament.getId(),
                tournament.getSlug(),
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
                tournament.getImageUrl(),
                confirmedTeams,
                userTeam
        );
    }
}
