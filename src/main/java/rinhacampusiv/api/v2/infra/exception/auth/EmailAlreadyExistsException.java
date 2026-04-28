package rinhacampusiv.api.v2.infra.exception.auth;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Email já utilizado");
    }
}