package rinhacampusiv.api.v2.domain.tournaments.teams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // Conta equipes ativas em um torneio — útil para validar se o torneio ainda tem vagas
    Long countByActiveTrueAndTournamentId(Long tournamentId);

    // Verifica se já existe equipe com o mesmo nome no mesmo torneio
    Boolean existsByNameAndTournamentId(String name, Long tournamentId);

    // Verifica se o capitão já possui equipe neste torneio
    Boolean existsByCaptainIdAndTournamentId(Long captainId, Long tournamentId);

    // Busca equipe pelo nome dentro de um torneio específico
    Optional<Team> findByNameAndTournamentId(String name, Long tournamentId);

    //Busca equipe por id do capitão e id do torneio
    Optional<Team> findByCaptainIdAndTournamentId(Long captainId, Long tournamentId);

    Boolean existsByNameIgnoreCaseAndTournamentId(String name, Long tournamentId);

    // Busca equipe com pagamentos já carregados — evita N+1 no fluxo de pagamento
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.payments WHERE t.id = :id")
    Optional<Team> findByIdWithPayments(@Param("id") Long id);

    //Busca equipes com pagamentos pendentes
    @Query("SELECT t FROM Team t JOIN FETCH t.payments WHERE t.status = 'PENDING_PAYMENT' ")
    List<Team> findAllPendingPayments();
    // Busca equipe com jogadores já carregados — útil para exibir detalhes da equipe
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players WHERE t.id = :id")
    Optional<Team> findByIdWithPlayers(@Param("id") Long id);

    // Busca todas as equipes de um torneio com jogadores e pagamentos — útil para dashboards
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players LEFT JOIN FETCH t.payments WHERE t.tournament.id = :tournamentId")
    List<Team> findAllByTournamentIdWithDetails(@Param("tournamentId") Long tournamentId);

    //verificar se existe equipes com o mesmo nome no mesmo torneio que não estejam canceladas
    Boolean existsByNameAndTournamentIdAndStatusNot(String name, Long tournamentId, TeamStatus status);

    Optional<Team> findByCaptainIdAndTournamentIdAndStatusNot(Long captainId, Long tournamentId, TeamStatus status);

    Boolean existsByNameAndTournamentIdAndIdNot(String name, Long tournamentId, Long teamId);
}