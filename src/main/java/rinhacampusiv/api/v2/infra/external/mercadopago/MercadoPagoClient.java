package rinhacampusiv.api.v2.infra.external.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.infra.exception.payments.MercadoPagoPaymentException;
import rinhacampusiv.api.v2.infra.exception.payments.PaymentNotFoundException;
import rinhacampusiv.api.v2.infra.loggers.PaymentLogger;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoClient {


    @Autowired
    private PaymentLogger logger;
    @Value("${mercadopago.access.token}")
    private String accessToken;
    
    private static final Long PAYMENT_EXPIRATION_TIME = 10L;


    private PaymentClient paymentClient;

    public void initPaymentClient() {
        this.paymentClient = new PaymentClient();
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    // ─── Consultar pagamento ───────────────────────────────────────────────────

    public Payment findPayment(String mercadoPagoId, boolean isWebHook, String payer) {
        logger.searchingPaymentInfoLog(mercadoPagoId, isWebHook);
        try {
            initPaymentClient();
            Payment payment = paymentClient.get(Long.valueOf(mercadoPagoId));

            logger.foundPaymentInfoLog(payment, payer);

            return payment;

        } catch (MPException | MPApiException e) {
            logger.paymentNotFoundErrorLog(mercadoPagoId, e);

            if (isWebHook) {
                return null;
            } else {
                throw new PaymentNotFoundException("Erro ao consultar pagamento: " + e.getMessage());
            }
        }
    }

    // ─── Cancelar pagamento ────────────────────────────────────────────────────

    public boolean cancelPayment(String mercadoPagoId, Long paymentEntityId) {
        logger.requestingCancelPaymentLog(mercadoPagoId);

        try {
            initPaymentClient();
            Payment canceledPayment = paymentClient.cancel(Long.valueOf(mercadoPagoId));

            logger.paymentCanceledLog(canceledPayment, paymentEntityId);

            return true;

        } catch (MPException | MPApiException e) {

            logger.paymentCancelErrorLog(mercadoPagoId, paymentEntityId, e);
            return false;
        }
    }

    // ─── Emitir pagamento (Pix) ────────────────────────────────────────────────

    public Payment emitPayment(PaymentRegistrationDataMercadoPago data, BigDecimal value) {
        String idempotencyKey = UUID.randomUUID().toString();

        logger.requestEmitPaymentInfoLog(data, value, idempotencyKey);

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put("x-idempotency-key", idempotencyKey);
        httpHeader.put("Content-Type",  "application/json");
        httpHeader.put("Accept",        "application/json");

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(httpHeader)
                .build();

        var payerData = PaymentPayerRequest.builder()
                .email(data.email())
                .firstName(data.nome())
                .lastName(data.sobrenome())
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF")
                                .number(data.cpf())
                                .build())
                .build();

        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(PAYMENT_EXPIRATION_TIME);

        PaymentCreateRequest paymentData = PaymentCreateRequest.builder()
                .transactionAmount(value)
                .description("Inscrição - Rinha da UFPB")
                .paymentMethodId("pix")
                .dateOfExpiration(expiresAt)
                    .notificationUrl("https://730d-2804-9f8-478a-b8b0-8855-23f0-7f79-4cf1.ngrok-free.app/webhook?source_news=webhooks")
                .payer(payerData)
                .build();

        try {
            initPaymentClient();

            Payment paymentCreated = paymentClient.create(paymentData, requestOptions);
            logger.paymentEmittedInfoLog(paymentCreated, data);

            return paymentCreated;
        } catch (MPApiException e) {
            logger.paymentEmitMPApiExceptionErrorLog(data, e, value, idempotencyKey);

            throw new MercadoPagoPaymentException("Erro ao criar pagamento no Mercado Pago");
        } catch (MPException e) {
            logger.paymentEmitMPExceptionErrorLog(data, e, value, idempotencyKey);

            throw new MercadoPagoPaymentException("Erro inesperado ao processar pagamento");
        }
    }
}
