package rinhacampusiv.api.v2.validators.tournament.team.ban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.TeamNotFoundException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TeamIsAlreadyBanned implements TournamentTeamBanValidator{

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void validar(Tournament tournament, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Equipe não encontrada"));

        boolean teamIsAlreadyBanned = team.getStatus() == TeamStatus.BANNED;
        if (teamIsAlreadyBanned) {
            throw new ValidatorException("Equpe já banida");
        }
    }
}
