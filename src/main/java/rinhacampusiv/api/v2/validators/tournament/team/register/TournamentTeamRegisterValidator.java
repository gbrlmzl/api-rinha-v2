package rinhacampusiv.api.v2.validators.tournament.team.register;

import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

public interface TournamentTeamRegisterValidator {
    void validate(TournamentRegistrationData data, Tournament tournament);
}
