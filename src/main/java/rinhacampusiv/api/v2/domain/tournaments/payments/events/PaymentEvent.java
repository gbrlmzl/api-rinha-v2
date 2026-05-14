package rinhacampusiv.api.v2.domain.tournaments.payments.events;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_events")
@Getter
@Setter
@NoArgsConstructor
public class PaymentEvent {

    public PaymentEvent(PaymentEntity payment, PaymentEventType type) {
        this.payment = payment;
        this.eventType = type;
    }

    public PaymentEvent(PaymentEntity payment, PaymentEventType type, MercadoPagoEventData mpData) {
        this(payment, type);
        this.mercadoPagoId = mpData.mpId();
        this.statusFromMp = mpData.statusFromMp();
        this.statusDetailFromMp = mpData.statusDetailFromMp();
        this.errorMessage = mpData.errorMessage();
    }

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

    @Column(name = "event_type", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentEventType eventType;

    @Column(name = "status_from_mp", length = 30)
    private String statusFromMp;

    @Column(name = "status_detail_from_mp", length = 50)
    private String statusDetailFromMp;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    private void prePersist() {
        this.receivedAt = OffsetDateTime.now();
    }
}