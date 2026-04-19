package rinhacampusiv.api.v2.domain.tournaments.payments.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    List<PaymentEvent> findByMercadoPagoId(String mercadoPagoId);
    List<PaymentEvent> findByPaymentId(Long paymentId);
}