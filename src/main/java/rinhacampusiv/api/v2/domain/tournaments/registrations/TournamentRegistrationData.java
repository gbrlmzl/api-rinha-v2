package rinhacampusiv.api.v2.domain.tournaments.registrations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamRegisterData;

public record TournamentRegistrationData(
        @Valid
        @NotNull(message = "Dados da equipe inválidos")
        TeamRegisterData teamData,

        @Valid
        @NotNull(message = "Dados do pagamento inválidos")
        PaymentRegistrationDataMercadoPago paymentData

) {
}
