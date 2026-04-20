package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

public record CheckRegistrationData(
        Boolean registered,
        TeamStatus teamStatus,
        TournamentStatus tournamentStatus,
        Boolean maxTeamsReached
) {

    public CheckRegistrationData(Boolean registered, TournamentStatus tournamentStatus, Boolean maxTeamsReached){
        this(registered, null, tournamentStatus, maxTeamsReached);
    }
}
