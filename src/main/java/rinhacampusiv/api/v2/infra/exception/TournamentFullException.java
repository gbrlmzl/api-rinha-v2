package rinhacampusiv.api.v2.infra.exception;

public class TournamentFullException extends RuntimeException {
    public TournamentFullException(String message) {
        super(message);
    }
}
