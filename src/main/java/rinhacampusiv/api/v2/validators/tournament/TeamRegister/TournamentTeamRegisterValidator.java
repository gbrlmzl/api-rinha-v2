package rinhacampusiv.api.v2.validators.tournament.TeamRegister;

import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

public interface TournamentTeamRegisterValidator {
    void validate(TournamentRegistrationData data, Tournament tournament);
}
