package rinhacampusiv.api.v2.domain.tournaments.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByMercadoPagoId(String mercadoPagoId);

    List<PaymentEntity> findAllByStatus(PaymentStatus status);

    Page<PaymentEntity> findAllByStatus(PaymentStatus status, Pageable pagination);

    Long countByStatus(PaymentStatus status);

    String id(Long id);

    @Query("SELECT p FROM PaymentEntity p JOIN p.team t WHERE t.tournament.id = :tournamentId AND (:status IS NULL OR p.status = :status)")
    Page<PaymentEntity> findByTournamentIdAndStatus(
            @Param("tournamentId") Long tournamentId,
            @Param("status") PaymentStatus status,
            Pageable pageable);
}
