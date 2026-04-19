package rinhacampusiv.api.v2.validators.tournamentRetryRegister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.validators.tournamentTeamRegister.TournamentTeamRegisterValidator;

@Component
public class TournamentRetryRegisterValidatorTeamsNumberInTournament implements TournamentRetryRegisterValidator {
    @Autowired
    TeamRepository teamRepository;


    @Override
    public void validate(Tournament tournament, Team team){
        if(teamRepository.countByActiveTrueAndTournamentId(tournament.getId()) > 7){
            throw new ValidatorException("O número máximo de equipes cadastradas no torneio foi atingido");
        }

    }
}
