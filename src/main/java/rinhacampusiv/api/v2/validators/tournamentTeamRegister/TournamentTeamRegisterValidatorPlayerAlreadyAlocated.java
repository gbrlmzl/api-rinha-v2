package rinhacampusiv.api.v2.validators.tournamentTeamRegister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.players.PlayerRepository;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.util.List;

@Component
public class TournamentTeamRegisterValidatorPlayerAlreadyAlocated implements TournamentTeamRegisterValidator {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void validate(TournamentRegistrationData data, Tournament tournament) {
        List<PlayerRegisterData> players = data.teamData().players();

        Long tournamentId = tournament.getId();

        for (PlayerRegisterData playerData : players) {

            if (playerRepository.existsBySchoolIdAndTeamTournamentId(
                    playerData.schoolId(), tournament.getId())) {

                throw new ValidatorException(
                        String.format(
                                "Já existe um jogador com a matrícula nº %s inscrito em uma equipe neste torneio",
                                playerData.schoolId()
                        )
                );

            } else if (playerRepository.existsByNicknameAndTeamTournamentId(
                    playerData.nickname(), tournament.getId())) {

                throw new ValidatorException(
                        String.format(
                                "Já existe um jogador com o nickname \"%s\" inscrito em uma equipe neste torneio",
                                playerData.nickname()
                        )
                );
            }
        }


    }
}
