package rinhacampusiv.api.v2.domain.tournaments.registrations;

public record PaymentConfirmationInfo(
        String teamName,
        String shieldUrl,
        String tournamentName,
        String tournamentStartDate) {
}
