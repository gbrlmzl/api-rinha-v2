package rinhacampusiv.api.v2.infra.exception.auth;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String msg) {
        super(msg);
    }
}
