package rinhacampusiv.api.v2.infra.exception;

public class SendPasswordResetEmailException extends RuntimeException {
    public SendPasswordResetEmailException(String message) {
        super(message);
    }
}
