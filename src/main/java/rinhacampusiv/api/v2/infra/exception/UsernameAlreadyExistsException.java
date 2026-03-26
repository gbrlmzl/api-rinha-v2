package rinhacampusiv.api.v2.infra.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Usuário já existe");
    }
}