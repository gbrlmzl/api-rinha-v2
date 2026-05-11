package rinhacampusiv.api.v2.validators.tournament.team.ban;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

public interface TournamentTeamBanValidator {
    void validar(Tournament tournament, Long teamId);
}
