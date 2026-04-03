package rinhacampusiv.api.v2.validators;

import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

public interface Validator {
    void validate(TournamentRegistrationData data, Tournament tournament);
}
