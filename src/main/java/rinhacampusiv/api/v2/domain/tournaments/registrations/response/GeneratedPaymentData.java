package rinhacampusiv.api.v2.domain.tournaments.registrations.response;

import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.utils.payments.QrCodeUtil;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record GeneratedPaymentData(
        String uuid,
        String qrCode,
        String qrCodeBase64,
        BigDecimal value,
        OffsetDateTime expiresAt

) {


    public GeneratedPaymentData(PaymentEntity payment) {
        this(
                payment.getUuid(),
                payment.getQrCode(),
                QrCodeUtil.generateBase64(payment.getQrCode()),
                payment.getValue(),
                payment.getExpiresAt()
        );
    }


}
