package rinhacampusiv.api.v2.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Component
public class StringToTournamentGameConverter implements Converter<String, TournamentGame> {
    @Override
    public TournamentGame convert(String source) {
        try {
            return TournamentGame.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String upper = source.toUpperCase();
            return switch (upper) {
                case "LOL" -> TournamentGame.LEAGUE_OF_LEGENDS;
                case "CS" -> TournamentGame.COUNTER_STRIKE;
                case "VAL" -> TournamentGame.VALORANT;
                default -> throw new ValidatorException("Jogo inválido: " + source);
            };

        }
    }
}
