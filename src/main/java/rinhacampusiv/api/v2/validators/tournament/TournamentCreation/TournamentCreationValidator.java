package rinhacampusiv.api.v2.validators.tournament.TournamentCreation;

import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;

public interface TournamentCreationValidator {
    void validar (TournamentCreationData tournamentData);
}
