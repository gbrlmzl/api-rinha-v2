package rinhacampusiv.api.v2.validators.tournamentTeamRegister;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.util.List;

@Component
public class TournamentTeamRegisterValidatorTeamPlayersAmount implements TournamentTeamRegisterValidator {
    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament){
        List<PlayerRegisterData> players = data.teamData().players();
        if(players.size() > 6){
            throw new ValidatorException("Quantidade máxima de jogadores: 6");
        } else if (players.size() < 5) {
            throw new ValidatorException("Quantidade mínima de jogadores: 5");
        }
    }
}
