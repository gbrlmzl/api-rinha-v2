package rinhacampusiv.api.v2.validators.tournament.team.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class TournamentTeamRegisterValidatorTeamsNumberInTournament implements TournamentTeamRegisterValidator {
    @Autowired
    TeamRepository teamRepository;


    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        if(teamRepository.countByActiveTrueAndTournamentId(tournament.getId()) > 7){
            throw new ValidatorException("O número máximo de equipes cadastradas no torneio foi atingido");
        }

    }
}
