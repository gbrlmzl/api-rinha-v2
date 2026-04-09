package rinhacampusiv.api.v2.infra.exception;

public class TournamentNotExistsException extends RuntimeException {
    public TournamentNotExistsException(String message) {
        super(message);
    }
}
