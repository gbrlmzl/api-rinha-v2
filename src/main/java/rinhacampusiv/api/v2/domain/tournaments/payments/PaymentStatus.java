package rinhacampusiv.api.v2.domain.tournaments.payments;

public enum PaymentStatus {
    pending,
    approved,
    expired;

    public static PaymentStatus fromMercadoPago(String mpStatus) {
        if (mpStatus == null) return pending;
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> approved;
            case "rejected", "cancelled" -> expired;
            case "expired" -> expired;
            default -> pending;
        };
    }
}
