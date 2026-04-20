package rinhacampusiv.api.v2.validators.tournament.team.ban;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentStatusIsFinishedValidator implements TournamentTeamBanValidator{
    @Override
    public void validar(Tournament tournament, Long teamId) {
        boolean tournamentStatusIsFinished = tournament.getStatus() == TournamentStatus.FINISHED;
        if (tournamentStatusIsFinished) {
            throw new ValidatorException("Não é possível banir equipes de um torneio encerrado");
        }
    }
}
