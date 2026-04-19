package rinhacampusiv.api.v2.domain.tournaments.payments.events;

public enum PaymentEventType {
    PROCESSED,         // verifyPayment rodou com sucesso
    IGNORED,           // status não era approved, ou body inválido
    ERROR;
}
