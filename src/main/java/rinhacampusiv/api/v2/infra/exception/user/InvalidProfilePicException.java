package rinhacampusiv.api.v2.infra.exception.user;

public class InvalidProfilePicException extends RuntimeException {
    public InvalidProfilePicException(String message) {
        super(message);
    }
}
