package rinhacampusiv.api.v2.domain.tournaments.registrations;

import com.mercadopago.resources.payment.Payment;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.utils.QrCodeUtil;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record GeneratedPaymentData(
        String mercadoPagoPaymentId,
        PaymentStatus status,
        String statusDetailed,
        BigDecimal value,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        String uuid,
        String qrCode,
        String qrCodeBase64

) {


    public GeneratedPaymentData(PaymentEntity data) {
        this(
                String.valueOf(data.getId()),
                data.getStatus(),
                data.getStatusDetail(),
                data.getValue(),
                data.getCreatedAt(),
                data.getExpiresAt(),
                data.getUuid(),
                data.getQrCode(),
                QrCodeUtil.generateBase64(data.getQrCode())

        );
    }


}
