package rinhacampusiv.api.v2.infra.exception;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String msg) {
        super(msg);
    }
}
