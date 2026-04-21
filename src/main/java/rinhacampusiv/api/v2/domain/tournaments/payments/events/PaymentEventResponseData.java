package rinhacampusiv.api.v2.domain.tournaments.payments.events;

import java.time.OffsetDateTime;

public record PaymentEventResponseData(
        Long id,
        String mercadoPagoId,
        PaymentEventType eventType,
        String statusFromMp,
        String statusDetailFromMp,
        String errorMessage,
        OffsetDateTime receivedAt
){
    public PaymentEventResponseData (PaymentEvent event){
        this(
                event.getId(),
                event.getMercadoPagoId(),
                event.getEventType(),
                event.getStatusFromMp(),
                event.getStatusDetailFromMp(),
                event.getErrorMessage(),
                event.getReceivedAt()
        );
    }
}