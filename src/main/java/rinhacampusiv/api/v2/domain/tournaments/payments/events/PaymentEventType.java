package rinhacampusiv.api.v2.domain.tournaments.payments.events;

public enum PaymentEventType {
    // Webhook
    PROCESSED,          // webhook processado → APPROVED
    IGNORED,            // webhook ignorado
    ERROR,              // falha no processamento

    // Ciclo interno
    PAYMENT_GENERATED,  // PIX gerado, aguardando pagamento
    EXPIRED_BY_JOB,     // job de expiração disparou
    CANCELED_BY_USER,   // usuário cancelou inscrição
    CANCELED_BY_ADMIN   // admin cancelou torneio
}
