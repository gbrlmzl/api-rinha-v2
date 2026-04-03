package rinhacampusiv.api.v2.domain.tournaments.players;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Verifica se o schoolId já está cadastrado em alguma equipe do torneio
    Boolean existsBySchoolIdAndTeamTournamentId(String schoolId, Long tournamentId);
}