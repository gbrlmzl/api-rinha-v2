package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import com.fasterxml.jackson.annotation.JsonCreator;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;

public enum TournamentStatus {
    OPEN,
    FULL,
    ONGOING,
    CANCELED,
    FINISHED;

    //Resolve a criação
    @JsonCreator
    public static TournamentStatus fromString(String source) {
        if (source == null) return null;

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
