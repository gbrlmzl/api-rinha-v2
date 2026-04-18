package rinhacampusiv.api.v2.domain.tournaments.payments;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentLogData(
        Long paymentId,
        String mercadoPagoId,
        String uuid,
        String teamName,
        String captainUsername,
        BigDecimal value,
        PaymentStatus status,
        String statusDetail,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        OffsetDateTime paidAt,
        String payer,
        boolean hasRawLog
) {
    public PaymentLogData(PaymentEntity p, boolean hasRawLog) {
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
                hasRawLog
        );
    }
}
