package rinhacampusiv.api.v2.infra.exception.users;

public class InvalidProfilePicException extends RuntimeException {
    public InvalidProfilePicException(String message) {
        super(message);
    }
}
