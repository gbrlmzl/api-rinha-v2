package rinhacampusiv.api.v2.domain.tournaments.teams;

import com.fasterxml.jackson.annotation.JsonCreator;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;

public enum TeamStatus {
    PENDING_PAYMENT,
    EXPIRED_PAYMENT,
    EXPIRED_PAYMENT_PROBLEM,
    READY,
    FINISHED,
    CANCELED,
    BANNED;

    @JsonCreator
    public static TeamStatus fromString(String source) {
        if (source == null) return null;

        try {
            return TeamStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String upper = source.toUpperCase();
            return switch (upper) {
                case "PENDING_PAYMENT" -> TeamStatus.PENDING_PAYMENT;
                case "EXPIRED_PAYMENT" -> TeamStatus.EXPIRED_PAYMENT;
                case "EXPIRED_PAYMENT_PROBLEM" -> TeamStatus.EXPIRED_PAYMENT_PROBLEM;
                case "READY" -> TeamStatus.READY;
                case "FINISHED" -> TeamStatus.FINISHED;
                case "CANCELED" -> TeamStatus.CANCELED;
                case "BANNED" -> TeamStatus.BANNED;
                default -> throw new ValidatorException("Status de equipe inválido: " + source);
            };
        }
    }
}
