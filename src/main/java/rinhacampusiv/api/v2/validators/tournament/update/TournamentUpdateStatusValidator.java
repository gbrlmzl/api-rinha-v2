package rinhacampusiv.api.v2.validators.tournament.update;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentUpdateStatusValidator implements TournamentUpdateValidator {

    @Override
    public void validar(Tournament tournament, TournamentUpdateData data) {
        TournamentStatus oldStatus = tournament.getStatus();
        TournamentStatus newStatus = data.status();

        if (newStatus == null || oldStatus == newStatus) return;

        if (newStatus == TournamentStatus.FULL) {
            throw new ValidatorException("O status FULL é definido automaticamente pelo sistema.");
        }

        if (newStatus == TournamentStatus.ONGOING) {
            throw new ValidatorException("O status ONGOING é definido automaticamente quando o torneio iniciar.");
        }

        if (newStatus == TournamentStatus.CANCELED) {
            throw new ValidatorException("Para cancelar um torneio utilize o endpoint de cancelamento.");
        }

        if (oldStatus == TournamentStatus.FINISHED) {
            throw new ValidatorException("Não é possível alterar o status de um torneio já finalizado.");
        }

        if (newStatus == TournamentStatus.OPEN && oldStatus != TournamentStatus.CANCELED) {
            throw new ValidatorException("Um torneio só pode ser reaberto (OPEN) se estiver cancelado.");
        }

        if (newStatus == TournamentStatus.FINISHED && oldStatus != TournamentStatus.ONGOING) {
            throw new ValidatorException("Um torneio só pode ser finalizado (FINISHED) se estiver em andamento (ONGOING).");
        }
    }
}
