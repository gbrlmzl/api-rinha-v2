package rinhacampusiv.api.v2.infra.exception.tournaments;

public class TournamentFullException extends RuntimeException {
    public TournamentFullException(String message) {
        super(message);
    }
}
