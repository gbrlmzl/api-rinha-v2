package rinhacampusiv.api.v2.validators.tournament.TournamentUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentNameUpdateAlreadyExists implements TournamentUpdateValidator{

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public void validar(Tournament tournament, TournamentUpdateData tournamentData) {
        if (tournamentData.name() != null) {
            boolean tournamentNameAlreadyExists = tournamentRepository.existsByNameAndGame(tournamentData.name(), tournamentData.game()) || tournament.getName().equals(tournamentData.name());
            if (tournamentNameAlreadyExists) {
                throw new ValidatorException("Já existe um torneio com esse nome");
            }
        }
    }
}
