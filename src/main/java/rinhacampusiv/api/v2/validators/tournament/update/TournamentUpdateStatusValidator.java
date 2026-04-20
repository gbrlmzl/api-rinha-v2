package rinhacampusiv.api.v2.validators.tournament.update;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.time.OffsetDateTime;

@Component
public class TournamentUpdateStatusValidator implements TournamentUpdateValidator {

    @Override
    public void validar(Tournament tournament, TournamentUpdateData data) {
        TournamentStatus oldStatus = tournament.getStatus();
        TournamentStatus newStatus = data.status();
        OffsetDateTime startsAt = data.startsAt() != null ? data.startsAt() : tournament.getStartsAt();

        if (newStatus == null || oldStatus == newStatus) return;

        // Regra 1: Se já acabou ou foi cancelado, está "congelado"
        if (oldStatus == TournamentStatus.FINISHED || oldStatus == TournamentStatus.CANCELED) {
            throw new ValidatorException("Não é possível alterar o status de um torneio já finalizado ou cancelado.");
        }

        // Regra 2: Não pode abrir se a data já passou
        if (newStatus == TournamentStatus.OPEN) {
            if (OffsetDateTime.now().isAfter(startsAt)) {
                throw new ValidatorException("Não pode reabrir (OPEN) um torneio cuja data de início já passou.");
            }
        }

        // Regra 3: Não pode finalizar antes da data de início
        if (newStatus == TournamentStatus.FINISHED) {
            if (OffsetDateTime.now().isBefore(startsAt)) {
                throw new ValidatorException("Não pode finalizar (FINISHED) um torneio antes do seu início. Utilize a opção Cancelar.");
            }
        }
    }
}
