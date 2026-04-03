package rinhacampusiv.api.v2.infra.exception.payments;

public class TeamWithoutPaymentException extends RuntimeException {
    public TeamWithoutPaymentException(String message) {
        super(message);
    }
}
