package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentRegistrationStatusData(
        @NotNull
        boolean registered,

        //Payment data
        PaymentStatus paymentStatus,
        String uuid,
        BigDecimal value,
        String qrCode,
        String qrCodeBase64,
        OffsetDateTime expiresAt

) {
        public TournamentRegistrationStatusData(boolean registered) {
        this(registered, null, null, null, null, null, null);
    }

        public TournamentRegistrationStatusData(boolean registered, PaymentStatus paymentStatus) {
        this(registered, paymentStatus, null, null, null, null, null);
    }

}
