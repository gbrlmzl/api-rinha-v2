package rinhacampusiv.api.v2.validators.tournament.TournamentCreation;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.time.OffsetDateTime;

@Component
public class TournamentDateStartsBeforeAHourValidator implements TournamentCreationValidator{

    private static final int HOUR_VALIDATOR = 1;

    @Override
    public void validar(TournamentCreationData tournamentData) {
        boolean tournamentDateStartsBeforeAHourValidator = tournamentData.startsAt().isBefore(OffsetDateTime.now().plusHours(1));
        if (tournamentDateStartsBeforeAHourValidator )
            throw new ValidatorException("O torneio não pode começar antes de " + HOUR_VALIDATOR + " hora(s)");
    }

}


