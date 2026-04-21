package rinhacampusiv.api.v2.domain.tournaments.registrations.request;

import jakarta.validation.constraints.NotNull;

public record CancelRegistrationDto(
        @NotNull
        Boolean cancelRegistration
) {
}
