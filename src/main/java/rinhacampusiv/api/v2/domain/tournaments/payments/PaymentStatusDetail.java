package rinhacampusiv.api.v2.domain.tournaments.payments;

public enum PaymentStatusDetail {
    WAITING_TRANSFER,
    ACCREDITED,
    EXPIRED,
    CANCELED_BY_USER;


    public static PaymentStatusDetail fromMercadoPago(String mpStatusDetail){
        if (mpStatusDetail == null) return WAITING_TRANSFER;
        return switch (mpStatusDetail.toLowerCase()) {
            case "pending_waiting_transfer" -> WAITING_TRANSFER;
            case "accredited" -> ACCREDITED;
            case "expired" -> EXPIRED;
            case "by_collector" -> CANCELED_BY_USER; //'cancelled' -> retorno da API do mercado pago
            default -> WAITING_TRANSFER;
        };


    }

}
