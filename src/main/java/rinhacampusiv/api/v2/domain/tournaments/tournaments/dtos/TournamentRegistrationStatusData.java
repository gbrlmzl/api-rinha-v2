package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentRegistrationStatusData(
        CheckRegistrationData registrationData,
        GeneratedPaymentData paymentData


) {

    public TournamentRegistrationStatusData(CheckRegistrationData registrationData) {
        this(registrationData, null);
    }


}
