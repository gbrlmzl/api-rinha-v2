package rinhacampusiv.api.v2.infra.exception.payments;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
