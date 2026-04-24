package rinhacampusiv.api.v2.validators.tournament.update;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.time.OffsetDateTime;

@Component
public class TournamentUpdateDatesValidator implements TournamentUpdateValidator {

    @Override
    public void validar(Tournament tournament, TournamentUpdateData data) {
        // Se ambos não foram informados, não há nada de novo para validar entre eles
        if (data.startsAt() == null && data.endsAt() == null) {
            return;
        }

        // Pega a nova data de início, se tiver. Se não, usa a do banco.
        OffsetDateTime startsAt = data.startsAt() != null ? data.startsAt() : tournament.getStartsAt();
        
        // Pega a nova data de fim, se tiver. Se não, usa a do banco.
        OffsetDateTime endsAt = data.endsAt() != null ? data.endsAt() : tournament.getEndsAt();

        if (startsAt != null && endsAt != null) {
            // Verifica se a data de término é pelo menos 1 hora depois do início
            if (!endsAt.isAfter(startsAt.plusHours(1))) {
                throw new ValidatorException("A data de término deve ser pelo menos 1 hora após a data de início.");
            }
        }
    }
}
