package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findTournamentById(Long id);

    Boolean existsByNameAndGame(String tournamentName, TournamentGame game);

    Boolean existsByGameAndStatus(TournamentGame tournamentGame, TournamentStatus status);
}
