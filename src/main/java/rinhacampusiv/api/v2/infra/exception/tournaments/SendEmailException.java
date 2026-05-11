package rinhacampusiv.api.v2.infra.exception.tournaments;

public class SendEmailException extends RuntimeException {
    public SendEmailException(String message) {
        super(message);
    }
}
