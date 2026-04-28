package rinhacampusiv.api.v2.infra.exception.users;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException() {
        super("Conta não ativada.\nPor favor, verifique seu email.");

    }

}
