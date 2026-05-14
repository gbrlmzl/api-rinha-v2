package rinhacampusiv.api.v2.validators.tournament.team.register.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.players.Player;
import rinhacampusiv.api.v2.domain.tournaments.players.PlayerRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;

import java.util.List;

@Component
public class TournamentRetryRegisterValidatorPlayerAlreadyAlocated implements TournamentRetryRegisterValidator {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void validate(Tournament tournament, Team team ) {
        List<Player> players = team.getPlayers();

        Long tournamentId = tournament.getId();

        for (Player player : players) {

            if(player.getSchoolId() != null) {
                if (playerRepository.existsBySchoolIdAndTeamTournamentIdAndTeamIdNot(
                        player.getSchoolId(), tournament.getId(), team.getId())) {

                    throw new ValidatorException(
                            String.format(
                                    "Já existe um jogador com a matrícula nº %s inscrito em uma equipe neste torneio",
                                    player.getSchoolId()
                            )
                    );

                }
            }


            if (playerRepository.existsByNicknameAndTeamTournamentIdAndTeamIdNot(
                    player.getName(), tournament.getId(), team.getId())) {

                throw new ValidatorException(
                        String.format(
                                "Já existe um jogador com o nickname \"%s\" inscrito em uma equipe neste torneio",
                                player.getNickname()
                        )
                );
            }
        }


    }
}