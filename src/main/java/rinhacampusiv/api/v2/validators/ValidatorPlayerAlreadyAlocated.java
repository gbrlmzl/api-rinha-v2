package rinhacampusiv.api.v2.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.players.PlayerRepository;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.util.List;

@Component
public class ValidatorPlayerAlreadyAlocated implements Validator{

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        List<PlayerRegisterData> players = data.teamData().players();

        for(PlayerRegisterData playerData : players){
            if(playerRepository.existsBySchoolIdAndTeamTournamentId(playerData.schoolId(), tournament.getId())){
                throw new ValidatorException("Jogador: " + playerData.playerName() + " já está inscrito em uma equipe neste torneio");
            }
        }

    }
}
