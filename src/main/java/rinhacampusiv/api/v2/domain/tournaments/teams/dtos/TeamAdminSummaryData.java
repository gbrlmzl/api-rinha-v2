package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

import java.time.OffsetDateTime;

public record TeamAdminSummaryData(
        Long id,
        String name,
        String shieldUrl,
        String captainUsername,
        TeamStatus status,
        boolean active,
        int playersCount,
        PaymentStatus lastPaymentStatus,
        OffsetDateTime createdAt
) {
    public TeamAdminSummaryData(Team team) {
        this(
                team.getId(),
                team.getName(),
                team.getShieldUrl(),
                team.getCaptain().getUsername(),
                team.getStatus(),
                team.isActive(),
                team.getPlayers().size(),
                team.getPayments().isEmpty() ? null : team.getPayments().getLast().getStatus(),
                team.getCreatedAt()
        );
    }
}