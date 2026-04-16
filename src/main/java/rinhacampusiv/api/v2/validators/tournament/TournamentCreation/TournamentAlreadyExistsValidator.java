package rinhacampusiv.api.v2.validators.tournament.TournamentCreation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentAlreadyExistsValidator implements TournamentCreationValidator{

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public void validar(TournamentCreationData tournamentData) {
        boolean tournamentAlreadyExists = tournamentRepository.existsByNameAndGame(tournamentData.name(), tournamentData.game());
        if (tournamentAlreadyExists)
            throw new ValidatorException("Torneio o com nome: " + tournamentData.name() + " já existe");
    }
}
