package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public record TournamentDetailData(
        String name,
        TournamentGame game,
        TournamentStatus status,
        Integer maxTeams,
        BigDecimal prizePool,
        String startsAt,
        String endsAt
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor que recebe a entidade e já formata
    public TournamentDetailData(Tournament tournament) {
        this(
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt() != null
                        ? tournament.getStartsAt().format(FORMATTER) : null,
                tournament.getEndsAt() != null
                        ? tournament.getEndsAt().format(FORMATTER) : null
        );
    }
}
