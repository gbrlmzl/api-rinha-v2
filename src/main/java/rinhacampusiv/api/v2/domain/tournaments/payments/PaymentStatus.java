package rinhacampusiv.api.v2.domain.tournaments.payments;

public enum PaymentStatus {
    PENDING,
    APPROVED,
    CANCELED;

    public static PaymentStatus fromMercadoPago(String mpStatus) {
        if (mpStatus == null) return PENDING;
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> APPROVED;
            case "cancelled", "canceled" -> CANCELED; //'cancelled' -> retorno da API do mercado pago
            default -> PENDING;
        };
    }
}
