package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.registrations.response.CheckRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;

public record TournamentRegistrationStatusData(
        CheckRegistrationData registrationData,
        GeneratedPaymentData paymentData


) {

    public TournamentRegistrationStatusData(CheckRegistrationData registrationData) {
        this(registrationData, null);
    }


}
