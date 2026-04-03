package rinhacampusiv.api.v2.infra.exception;

import lombok.Getter;

@Getter
public class MercadoPagoPaymentException extends RuntimeException {

    private final int statusCode;
    private final String detailedError;

    public MercadoPagoPaymentException(String message, int statusCode, String detailedError) {
        super(message + " " + detailedError);
        this.statusCode = statusCode;
        this.detailedError = detailedError;
    }
}
