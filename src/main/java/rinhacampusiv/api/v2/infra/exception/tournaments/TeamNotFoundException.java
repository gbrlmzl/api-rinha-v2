package rinhacampusiv.api.v2.infra.exception.tournaments;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
