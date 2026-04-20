package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentRegistrationStatus(
        @NotNull
        boolean registered,

        //Payment data
        TeamStatus teamStatus,
        String uuid,
        BigDecimal value,
        String qrCode,
        String qrCodeBase64,
        OffsetDateTime expiresAt

) {
        public TournamentRegistrationStatus(boolean registered) {
        this(registered, null, null, null, null, null, null);
    }

        public TournamentRegistrationStatus(boolean registered, TeamStatus teamStatus) {
        this(registered, teamStatus, null, null, null, null, null);
    }

}
