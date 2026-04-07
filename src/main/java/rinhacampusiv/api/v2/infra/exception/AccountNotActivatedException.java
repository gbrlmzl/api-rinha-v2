package rinhacampusiv.api.v2.infra.exception;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException() {
        super("Conta não ativada.\nPor favor, verifique seu email.");

    }

}
