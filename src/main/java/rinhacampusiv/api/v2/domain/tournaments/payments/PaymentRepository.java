package rinhacampusiv.api.v2.domain.tournaments.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByMercadoPagoId(String mercadoPagoId);

    List<PaymentEntity> findAllByStatus(String status);

    Page<PaymentEntity> findAllByStatus(String status, Pageable pagination);

    Long countByStatus(String status);

    String id(Long id);
}
