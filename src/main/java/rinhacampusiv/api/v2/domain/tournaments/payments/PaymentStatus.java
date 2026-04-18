package rinhacampusiv.api.v2.domain.tournaments.payments;

public enum PaymentStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED;

    public static PaymentStatus fromMercadoPago(String mpStatus) {
        if (mpStatus == null) return PENDING;
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> APPROVED;
            case "rejected", "cancelled" -> REJECTED;
            case "expired" -> EXPIRED;
            default -> PENDING;
        };
    }
}
