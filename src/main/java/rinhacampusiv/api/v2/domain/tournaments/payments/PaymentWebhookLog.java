package rinhacampusiv.api.v2.domain.tournaments.payments;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_webhook_logs")
@Getter
@Setter
@NoArgsConstructor
public class PaymentWebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @Column(name = "mercado_pago_id", length = 100)
    private String mercadoPagoId;

    @Column(name = "received_at", updatable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "raw_payload", nullable = false)
    private String rawPayload;

    @Column(name = "processing_outcome", length = 20, nullable = false)
    private String processingOutcome;  // "PROCESSED", "IGNORED", "ERROR"

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    private void prePersist() {
        this.receivedAt = OffsetDateTime.now();
    }
}
