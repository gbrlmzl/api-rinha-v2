package rinhacampusiv.api.v2.domain.tournaments.registrations.response;

import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

public record CheckRegistrationData(
        Long tournamentId,
        Boolean registered,
        TeamStatus teamStatus,
        Integer teamPlayersAmount,
        TournamentStatus tournamentStatus,
        Boolean maxTeamsReached
) {

    public CheckRegistrationData(Long tournamentId, Boolean registered, TournamentStatus tournamentStatus, Boolean maxTeamsReached){
        this(tournamentId, registered, null, null, tournamentStatus, maxTeamsReached);
    }
}
