package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentRegistrationStatus(
        @NotNull
        boolean registered,

        //Payment data
        String paymentStatus,
        String uuid,
        BigDecimal value,
        String qrCode,
        String qrCodeBase64,
        OffsetDateTime expiresAt

) {
        public TournamentRegistrationStatus(boolean registered) {
                this(registered, null, null, null, null, null, null);
        }

        public TournamentRegistrationStatus(boolean registered, String paymentStatus) {
                this(registered, paymentStatus, null, null, null, null, null);
        }

        /*public TournamentRegistrationStatus(boolean registered, String paymentStatus, String uuid, BigDecimal value, String qrCode, String qrCodeBase64, OffsetDateTime expiresAt) {
                this(registered ,paymentStatus, uuid, value, qrCode, qrCodeBase64,expiresAt )
        }*/


}
