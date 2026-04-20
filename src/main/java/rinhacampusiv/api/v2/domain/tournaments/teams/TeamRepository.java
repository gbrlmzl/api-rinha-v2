package rinhacampusiv.api.v2.domain.tournaments.teams;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // Conta equipes ativas em um torneio — útil para validar se o torneio ainda tem vagas
    Integer countByActiveTrueAndTournamentId(Long tournamentId);

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

    // Busca equipe com jogadores já carregados — útil para exibir detalhes da equipe
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players WHERE t.id = :id")
    Optional<Team> findByIdWithPlayers(@Param("id") Long id);

    // Busca todas as equipes de um torneio com jogadores e pagamentos — útil para dashboards
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players LEFT JOIN FETCH t.payments WHERE t.tournament.id = :tournamentId")
    List<Team> findAllByTournamentIdWithDetails(@Param("tournamentId") Long tournamentId);

    //Busca equipes com pagamentos pendentes
    @Query("SELECT t FROM Team t JOIN FETCH t.payments WHERE t.status = 'PENDING_PAYMENT' ")
    List<Team> findAllPendingPayments();

    Integer countByTournamentIdAndStatus(Long tournamentId, TeamStatus status);

    Integer countByTournamentId(Long id);


    //verificar se existe equipes com o mesmo nome no mesmo torneio que não estejam canceladas
    Boolean existsByNameAndTournamentIdAndStatusNot(String name, Long tournamentId, TeamStatus status);

    // Busca as equipes onde o usuário está, seja ele o capitão OU um dos jogadores
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN t.players p WHERE (p.user.email = :email OR t.captain.email = :email) AND t.tournament.game = :game")
    Page<Team> findTeamsByUserEmailAndGame(@Param("email") String email, @Param("game") TournamentGame game, Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Team t LEFT JOIN t.players p " +
            "WHERE t.tournament.id = :tournamentId " +
            "AND (t.captain.email = :email OR p.user.email = :email)")
    boolean existsByTournamentIdAndUserEmail(@Param("tournamentId") Long tournamentId, @Param("email") String email);

    Page<Team> findByTournamentId(Long tournamentId, Pageable pageable);

    Optional<Team> findByCaptainIdAndTournamentIdAndStatusNot(Long captainId, Long tournamentId, TeamStatus status);

    Boolean existsByNameAndTournamentIdAndIdNot(String name, Long tournamentId, Long teamId);

    Boolean existsByNameAndTournamentIdAndStatusNotIn(String name, Long tournamentId, List<TeamStatus> status);

}
