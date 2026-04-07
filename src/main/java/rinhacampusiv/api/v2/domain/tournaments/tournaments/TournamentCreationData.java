package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TournamentCreationData(
        @NotBlank
        String name,

        @NotNull
        TournamentGame game,

        @NotNull
        Integer maxTeams,

        @NotNull
        BigDecimal prizePool,

        @NotNull
        @FutureOrPresent
        OffsetDateTime startsAt,

        @NotNull
        @Future
        OffsetDateTime endsAt




) {
}
