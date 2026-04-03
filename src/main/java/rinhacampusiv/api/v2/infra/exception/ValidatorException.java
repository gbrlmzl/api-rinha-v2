package rinhacampusiv.api.v2.infra.exception;

public class ValidatorException extends RuntimeException {
    public ValidatorException(String msg) {
        super(msg);
    }
}
