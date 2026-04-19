package rinhacampusiv.api.v2.validators.tournament.tournamentRetryRegister;

import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

public interface TournamentRetryRegisterValidator {
    void validate(Tournament tournament, Team team );
}
