package rinhacampusiv.api.v2.validators.tournament.TournamentUpdate;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentUpdateDateStartsValidator implements TournamentUpdateValidator {

    private static final long HOUR_VALIDATOR = 1;


    @Override
    public void validar(Tournament tournament, TournamentUpdateData tournamentData) {

        if (tournamentData.startsAt() != null) {

            boolean newDateStartsBeforeCreationData = tournamentData.startsAt().isBefore(tournament.getCreatedAt().plusHours(1));
            if (newDateStartsBeforeCreationData)
                throw new ValidatorException("Não é possível alterar a hora do torneio para " + HOUR_VALIDATOR + " hora(s) depois do horário de criação(" + tournament.getStartsAt().toLocalTime() + ")" );

            boolean newDateIsEqualCreationData = tournamentData.startsAt() != tournament.getStartsAt();
            if (newDateIsEqualCreationData)
                throw new ValidatorException("O torneio não pode começar começar na mesma hora que já existia: " + tournament.getStartsAt().toLocalTime());
        }
    }
}


