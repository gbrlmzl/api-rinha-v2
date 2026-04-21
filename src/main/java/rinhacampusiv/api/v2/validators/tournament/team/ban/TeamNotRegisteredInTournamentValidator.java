package rinhacampusiv.api.v2.validators.tournament.team.ban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.infra.exception.TeamNotFoundException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TeamNotRegisteredInTournamentValidator implements TournamentTeamBanValidator{

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void validar(Tournament tournament, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Equipe não encontrada"));

        boolean teamNotRegisteredInTournament = !team.getTournament().getId().equals(tournament.getId());
        if (teamNotRegisteredInTournament) {
            throw new ValidatorException("Equipe não pertence a este torneio");
        }
    }
}
