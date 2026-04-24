package rinhacampusiv.api.v2.domain.tournaments.payments.events;

public record MercadoPagoEventData(
        String mpId,
        String statusFromMp,
        String statusDetailFromMp,
        String errorMessage
) {
    public static MercadoPagoEventData ignored(String mpId, String error) {
        return new MercadoPagoEventData(mpId, null, null, error);
    }

    public static MercadoPagoEventData ignoredWithStatus(String mpId, String statusFromMp) {
        return new MercadoPagoEventData(mpId, statusFromMp, null, null);
    }

    public static MercadoPagoEventData processed(String mpId, String statusFromMp, String statusDetailFromMp) {
        return new MercadoPagoEventData(mpId, statusFromMp, statusDetailFromMp, null);
    }

    public static MercadoPagoEventData error(String mpId, String statusFromMp, String statusDetailFromMp, String errorMessage) {
        return new MercadoPagoEventData(mpId, statusFromMp, statusDetailFromMp, errorMessage);
    }
}
