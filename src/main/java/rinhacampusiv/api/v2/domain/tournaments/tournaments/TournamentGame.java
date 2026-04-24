package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import com.fasterxml.jackson.annotation.JsonCreator;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

public enum TournamentGame {
    LEAGUE_OF_LEGENDS,
    COUNTER_STRIKE,
    VALORANT;

    @JsonCreator
    public static TournamentGame fromString(String source) {
        if (source == null) return null;

        try {
            return TournamentGame.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String upper = source.toUpperCase();
            return switch (upper) {
                case "LOL", "LEAGUE_OF_LEGENDS" -> TournamentGame.LEAGUE_OF_LEGENDS;
                case "CS", "CS2", "COUNTER-STRIKE" -> TournamentGame.COUNTER_STRIKE;
                case "VAL", "VALORANT" -> TournamentGame.VALORANT;
                default -> throw new ValidatorException("Jogo inválido: " + source);
            };
        }
    }
}
