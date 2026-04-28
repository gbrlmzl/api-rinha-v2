package rinhacampusiv.api.v2.infra.exception.payments;

import lombok.Getter;

@Getter
public class MercadoPagoPaymentException extends RuntimeException {

    private final Integer statusCode;
    private final String detailedError;

    public MercadoPagoPaymentException(String message, Integer statusCode, String detailedError) {
        super(message + " " + detailedError);
        this.statusCode = statusCode;
        this.detailedError = detailedError;
    }
    public MercadoPagoPaymentException(String message) {
        super(message);
        this.statusCode = null;
        this.detailedError = null;
    }
}
