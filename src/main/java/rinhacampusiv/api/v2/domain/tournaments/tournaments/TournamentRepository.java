package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Boolean existsByNameAndGame(String tournamentName, TournamentGame game);

    Page<Tournament> findByGame(TournamentGame game, Pageable pageable);

    Page<Tournament> findByGameAndStatusIn(TournamentGame game, List<TournamentStatus> listStatus, Pageable pageable);

}
