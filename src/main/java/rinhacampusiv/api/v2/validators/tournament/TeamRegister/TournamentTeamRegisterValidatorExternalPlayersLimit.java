package rinhacampusiv.api.v2.validators.tournament.TeamRegister;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.util.List;

@Component
public class TournamentTeamRegisterValidatorExternalPlayersLimit implements TournamentTeamRegisterValidator {

    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        int count = 0;
        List<PlayerRegisterData> players = data.teamData().players();
        for(PlayerRegisterData playerData : players){
            if(!playerData.externalPlayer()){ //Caso o jogador não seja externo, aumenta a contagem
                count += 1;
            }
        }
        if(count < 1 ){
            throw new ValidatorException("A equipe deve possuir ao menos um jogador matriculado na UFPB");
        }
    }
}
