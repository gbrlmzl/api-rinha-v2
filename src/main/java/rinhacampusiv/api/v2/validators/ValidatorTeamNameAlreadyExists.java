package rinhacampusiv.api.v2.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class ValidatorTeamNameAlreadyExists implements Validator{

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        String teamName = data.teamData().teamName();
        if(teamRepository.existsByNameAndTournamentId(teamName, tournament.getId())){
           throw new ValidatorException(String.format("Já existe uma equipe com o nome \"%s\" cadastrada neste torneio", teamName));
        }
    }
}
