package rinhacampusiv.api.v2.infra.loggers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class PaymentLogger {

    private static final Logger logger = LoggerFactory.getLogger(PaymentLogger.class);


    //Consulta de pagamento
    public void searchingPaymentInfoLog(String mercadoPagoId, boolean isWebHook) {
        logger.info("[MP] Consultando pagamento | mercadoPagoId={} | origem={}",
                mercadoPagoId, isWebHook ? "webhook" : "api");
    }


    public void foundPaymentInfoLog(Payment payment) {
        logger.info("[MP] Pagamento encontrado | mercadoPagoId={} | status={} | statusDetail={} | valor={} | pagador={} | email={} | dataCriacao={} | dataAprovacao={}",
                payment.getId(),
                payment.getStatus(),
                payment.getStatusDetail(),
                payment.getTransactionAmount(),
                payment.getPayer() != null ? payment.getPayer().getFirstName() + " " + payment.getPayer().getLastName() : "N/A",
                payment.getPayer() != null ? payment.getPayer().getEmail() : "N/A",
                payment.getDateCreated(),
                payment.getDateApproved()
        );
    }

    public void paymentNotFoundErrorLog(String mercadoPagoId, Exception e) {
        logger.error("[MP] Falha ao consultar pagamento | mercadoPagoId={} | erro={}", mercadoPagoId, e.getMessage());
    }


    //Cancelamento
    public void requestingCancelPaymentLog(String mercadoPagoId) {
        logger.info("[MP] Solicitando cancelamento | mercadoPagoId={}", mercadoPagoId);
    }

    public void paymentCanceledLog(Payment canceledPayment, Long paymentEntityId) {
        logger.info("[MP] Pagamento cancelado com sucesso | mercadoPagoId={} | paymentEntityId={} | statusFinal={} | statusDetail={} | valorDevolvido={} | dataCancelamento={}",
                canceledPayment.getId(),
                paymentEntityId,
                canceledPayment.getStatus(),
                canceledPayment.getStatusDetail(),
                canceledPayment.getTransactionAmount(),
                OffsetDateTime.now()
        );
    }

    public void paymentCancelErrorLog(String mercadoPagoId, Long paymentEntityId, Exception e) {
        logger.error("[MP] Falha ao cancelar pagamento | mercadoPagoId={} | paymentEntityId={} | erro={}",
                mercadoPagoId, paymentEntityId, e.getMessage());
    }

    //Emissão de pagamento
    public void requestEmitPaymentInfoLog(PaymentRegistrationDataMercadoPago data, BigDecimal value, String idempotencyKey) {
        logger.info("[MP] Iniciando emissão de pagamento | pagador={} {} | email={} | cpf={} | valor={} | idempotencyKey={}",
                data.nome(),
                data.sobrenome(),
                data.email(),
                maskCpf(data.cpf()),
                value,
                idempotencyKey
        );
    }

    public void paymentEmittedInfoLog(Payment payment) {
        logger.info("[MP] Pagamento emitido com sucesso | mercadoPagoId={} | status={} | statusDetail={} | valor={} | pagador={} | email={} | dataCriacao={}",
                payment.getId(),
                payment.getStatus(),
                payment.getStatusDetail(),
                payment.getTransactionAmount(),
                payment.getPayer() != null ? payment.getPayer().getFirstName() + " " + payment.getPayer().getLastName() : "N/A",
                payment.getPayer() != null ? payment.getPayer().getEmail() : "N/A",
                payment.getDateCreated()
        );
    }

    public void paymentEmitMPApiExceptionErrorLog(PaymentRegistrationDataMercadoPago data, MPApiException e, BigDecimal value, String idempotencyKey) {
        logger.error("[MP] Erro na API do Mercado Pago ao criar pagamento | statusCode={} | pagador={} {} | email={} | valor={} | idempotencyKey={} | detalhes={}",
                e.getApiResponse().getStatusCode(),
                data.nome(),
                data.sobrenome(),
                data.email(),
                value,
                idempotencyKey,
                e.getApiResponse().getContent()
        );
    }

    public void paymentEmitMPExceptionErrorLog(PaymentRegistrationDataMercadoPago data, Exception e, BigDecimal value, String idempotencyKey) {
        logger.error("[MP] Erro inesperado ao criar pagamento | pagador={} {} | email={} | valor={} | idempotencyKey={} | erro={}",
                data.nome(),
                data.sobrenome(),
                data.email(),
                value,
                idempotencyKey,
                e.getMessage()
        );
    }

    // ─── Utilitário ────────────────────────────────────────────────────────────

    /**
     * Mascara o CPF nos logs para evitar exposição de dados sensíveis.
     * Exemplo: 123.456.789-00 → ***.***.789-**
     */
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 6) return "***";
        String digits = cpf.replaceAll("[^0-9]", "");
        if (digits.length() != 11) return "***";
        return "***.***.%s-**".formatted(digits.substring(6, 9));
    }


}
