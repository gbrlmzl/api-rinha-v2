package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentNotFoundException;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Boolean existsByNameAndGame(String tournamentName, TournamentGame game);

    Page<Tournament> findByGame(TournamentGame game, Pageable pageable);

    Page<Tournament> findByGameAndStatusIn(TournamentGame game, List<TournamentStatus> listStatus, Pageable pageable);

    boolean existsByNameAndGameAndIdNot(String name, TournamentGame game, Long id);

    Optional<Tournament> findBySlug(String slug);

    default Tournament findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }

    default Tournament findBySlugOrThrow(String slug) {
        return findBySlug(slug).orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }
}
