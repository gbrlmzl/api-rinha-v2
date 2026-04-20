package rinhacampusiv.api.v2.domain.tournaments.players;

import org.springframework.data.jpa.repository.JpaRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Verifica se o schoolId já está cadastrado em alguma equipe do torneio
    Boolean existsBySchoolIdAndTeamTournamentId(String schoolId, Long tournamentId);

    Boolean existsByNicknameAndTeamTournamentId(String playerNickName, Long tournamentId);

    Boolean existsBySchoolIdAndTeamTournamentIdAndTeamIdNot(String schoolId, Long tournamentId, Long teamId);

    Boolean existsByNicknameAndTeamTournamentIdAndTeamIdNot(String playerNickName, Long tournamentId, Long teamId);

    Boolean existsBySchoolIdAndTeamTournamentIdAndTeamStatusNot(String schoolId, Long tournamentId, TeamStatus status);

    Boolean existsByNicknameAndTeamTournamentIdAndTeamStatusNot(String playerNickName, Long tournamentId, TeamStatus status);
}