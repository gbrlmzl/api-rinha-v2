package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;

import java.time.OffsetDateTime;
import java.util.Comparator;

public record MyTournamentsSummaryData(
        Long id,
        String slug,
        String tournamentName,
        String teamName,
        TournamentGame game,
        TournamentStatus status,
        TeamStatus teamStatus,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        OffsetDateTime expiresAtPayment
) {

    public MyTournamentsSummaryData(Tournament tournament, Team team){
        this(
                tournament.getId(),
                tournament.getSlug(),
                tournament.getName(),
                team.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                team.getStatus(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                team.getPayments().stream()
                        .filter(payment -> payment.getStatus() == PaymentStatus.PENDING)
                        .sorted(Comparator.comparing(PaymentEntity::getExpiresAt).reversed())
                        .map(PaymentEntity::getExpiresAt)
                        .findFirst()
                        .orElse(null)
        );
    }
}
