package rinhacampusiv.api.v2.infra.exception.auth;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Usuário já existe");
    }
}