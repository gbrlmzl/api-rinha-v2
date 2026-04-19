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
public class TournamentRetryRegisterValidatorTeamNameAlreadyExists implements TournamentRetryRegisterValidator {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void validate(Tournament tournament, Team team){
        String teamName = team.getName();
        if(teamRepository.existsByNameAndTournamentIdAndIdNot(teamName, tournament.getId(), team.getId())){
           throw new ValidatorException(String.format("Já existe uma equipe com o nome \"%s\" cadastrada neste torneio", teamName));
        }
    }
}
