package rinhacampusiv.api.v2.domain.tournaments.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentWebhookLogRepository extends JpaRepository<PaymentWebhookLog, Long> {
    List<PaymentWebhookLog> findByMercadoPagoId(String mercadoPagoId);
    List<PaymentWebhookLog> findByPaymentId(Long paymentId);
}
