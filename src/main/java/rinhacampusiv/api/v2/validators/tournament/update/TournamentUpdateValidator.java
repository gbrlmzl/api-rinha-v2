package rinhacampusiv.api.v2.validators.tournament.update;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;

public interface TournamentUpdateValidator {
    void validar(Tournament tournament, TournamentUpdateData data);
}
