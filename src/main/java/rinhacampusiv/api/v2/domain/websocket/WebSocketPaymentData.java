package rinhacampusiv.api.v2.domain.websocket;

import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;

import java.math.BigDecimal;

public record WebSocketPaymentData(
        String status,
        String message,
        String mercadoPagoId,
        BigDecimal value,
        String uuid

) {
    public WebSocketPaymentData(PaymentEntity paymentData, String message) {
        this(
                paymentData.getStatus(),
                message,
                paymentData.getMercadoPagoId(),
                paymentData.getValue(),
                paymentData.getUuid()
        );
    }
}
