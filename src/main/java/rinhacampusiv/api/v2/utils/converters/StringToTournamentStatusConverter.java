package rinhacampusiv.api.v2.utils.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class StringToTournamentStatusConverter implements Converter<String, TournamentStatus> {
    @Override
    public TournamentStatus convert(String source) {
        try {
            return TournamentStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String upper = source.toUpperCase();
            return switch (upper) {
                case "OPEN" -> TournamentStatus.OPEN;
                case "FULL" -> TournamentStatus.FULL;
                case "ONGOING" -> TournamentStatus.ONGOING;
                case "CANCELED" -> TournamentStatus.CANCELED;
                case "FINISHED" -> TournamentStatus.FINISHED;
                default -> throw new ValidatorException("Status de torneio inválido: " + source);
            };

        }
    }
}
