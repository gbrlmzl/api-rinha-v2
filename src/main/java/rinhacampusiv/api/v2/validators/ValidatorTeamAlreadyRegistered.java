package rinhacampusiv.api.v2.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

import java.util.Optional;

@Component
public class ValidatorTeamAlreadyRegistered implements Validator{

    @Autowired
    TeamRepository teamRepository;

    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        TeamRegisterData teamDto = data.teamData();
        Optional<Team> verifyTeamInTournament = teamRepository.findByNameAndTournamentId(
                teamDto.teamName(), tournament.getId()) ;

    }
}
