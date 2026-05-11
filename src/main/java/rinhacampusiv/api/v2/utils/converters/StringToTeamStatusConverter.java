package rinhacampusiv.api.v2.utils.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;

@Component
public class StringToTeamStatusConverter implements Converter<String, TeamStatus> {
    @Override
    public TeamStatus convert(String source) {
        try {
            return TeamStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String upper = source.toUpperCase();
            return switch (upper) {
                case "PENDING_PAYMENT", "PENDING" -> TeamStatus.PENDING_PAYMENT;
                case "EXPIRED_PAYMENT", "EXPIRED" -> TeamStatus.EXPIRED_PAYMENT;
                case "EXPIRED_PAYMENT_PROBLEM", "EXPIRED_PROBLEM" -> TeamStatus.EXPIRED_PAYMENT_PROBLEM;
                case "READY" -> TeamStatus.READY;
                case "FINISHED" -> TeamStatus.FINISHED;
                case "CANCELED" -> TeamStatus.CANCELED;
                case "BANNED" -> TeamStatus.BANNED;
                default -> throw new ValidatorException("Status de equipe inválido: " + source);
            };
        }
    }
}
