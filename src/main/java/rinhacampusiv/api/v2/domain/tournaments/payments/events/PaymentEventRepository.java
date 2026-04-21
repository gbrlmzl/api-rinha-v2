package rinhacampusiv.api.v2.domain.tournaments.payments.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    List<PaymentEvent> findByMercadoPagoId(String mercadoPagoId);
    List<PaymentEvent> findByPaymentId(Long paymentId);

    @Query(value = """
    SELECT * FROM payment_events
    WHERE id IN (
        SELECT DISTINCT ON (payment_id) id
        FROM payment_events
        WHERE payment_id IN :paymentIds
        ORDER BY payment_id, received_at DESC
    )
    """, nativeQuery = true)
    List<PaymentEvent> findLastEventsByPaymentIds(@Param("paymentIds") List<Long> paymentIds);
}