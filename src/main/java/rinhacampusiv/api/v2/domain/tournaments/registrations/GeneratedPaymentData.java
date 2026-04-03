package rinhacampusiv.api.v2.domain.tournaments.registrations;

import com.mercadopago.resources.payment.Payment;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record GeneratedPaymentData(
        String mercadoPagoPaymentId,
        String status,
        String statusDetailed,
        BigDecimal value,
        OffsetDateTime createdAt,
        String uuid,
        String qrCode
        //String qrCodeBase64

) {
    public GeneratedPaymentData(PaymentEntity data) {
        this(
                String.valueOf(data.getId()),
                data.getStatus(),
                data.getStatusDetail(),
                data.getValue(),
                data.getCreatedAt(),
                data.getUuid(),
                data.getQrCode()

        );
    }
}
