package rinhacampusiv.api.v2.domain.tournaments.payments.events;

import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatusDetail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentEventData(
        Long paymentId,
        String mercadoPagoId,
        String uuid,
        String teamName,
        String captainUsername,
        BigDecimal value,
        PaymentStatus status,
        PaymentStatusDetail statusDetail,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        OffsetDateTime paidAt,
        String payer,
        PaymentEventType lastEventType
) {
    public PaymentEventData(PaymentEntity p, PaymentEventType lastEventType) {
        this(
                p.getId(),
                p.getMercadoPagoId(),
                p.getUuid(),
                p.getTeam().getName(),
                p.getTeam().getCaptain().getUsername(),
                p.getValue(),
                p.getStatus(),
                p.getStatusDetail(),
                p.getCreatedAt(),
                p.getExpiresAt(),
                p.getPaidAt(),
                p.getPayer(),
                lastEventType
        );
    }
}
