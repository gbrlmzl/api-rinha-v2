package rinhacampusiv.api.v2.validators.tournament.update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;

@Component
@RequiredArgsConstructor
public class TournamentUpdateMaxTeamsValidator implements TournamentUpdateValidator {

    private final TeamRepository teamRepository;

    @Override
    public void validar(Tournament tournament, TournamentUpdateData data) {
        if (data.maxTeams() == null) return;

        TournamentStatus status = tournament.getStatus();
        if (status != TournamentStatus.OPEN && status != TournamentStatus.FULL) {
            throw new ValidatorException("O número máximo de equipes só pode ser alterado enquanto o torneio estiver OPEN ou FULL.");
        }

        Integer activeTeams = teamRepository.countByActiveTrueAndTournamentId(tournament.getId());
        if (data.maxTeams() < activeTeams) {
            throw new ValidatorException(
                    "O número máximo de equipes não pode ser menor que o número de equipes já inscritas (" + activeTeams + ")."
            );
        }
    }
}