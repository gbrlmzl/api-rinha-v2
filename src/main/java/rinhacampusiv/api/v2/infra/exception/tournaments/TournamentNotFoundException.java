package rinhacampusiv.api.v2.infra.exception.tournaments;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(String message) {
        super(message);
    }
}
