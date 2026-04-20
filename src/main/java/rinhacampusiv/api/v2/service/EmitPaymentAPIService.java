package rinhacampusiv.api.v2.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.infra.exception.MercadoPagoPaymentException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class EmitPaymentAPIService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    public Payment emitPayment (PaymentRegistrationDataMercadoPago data, BigDecimal value){
        MercadoPagoConfig.setAccessToken(accessToken);

        PaymentClient client = new PaymentClient();

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put("x-idempotency-key", UUID.randomUUID().toString());
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Accept", "application/json");

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

        PaymentCreateRequest paymentData = PaymentCreateRequest.builder()
                .transactionAmount(value)
                .description("Inscrição - Rinha da UFPB")
                .paymentMethodId("pix")
                .dateOfExpiration(OffsetDateTime.now().plusMinutes(6))
                .notificationUrl("https://6da9-2804-9f8-4783-e3a0-19ff-20d4-7d62-8a86.ngrok-free.app/webhook?source_news=webhooks") //Notificações apenas via webhook
                .payer(payerData)
                .build();

        try {
            //pagamento - Payment
            return client.create(paymentData, requestOptions);
        } catch (MPApiException e) {
            throw new MercadoPagoPaymentException(
                    "Erro ao criar pagamento no Mercado Pago",
                    e.getApiResponse().getStatusCode(),
                    "Detalhes: " + e.getApiResponse().getContent() //Detalhes do erro apenas para debug
            );
        } catch (MPException e) {
            throw new MercadoPagoPaymentException("Erro inesperado ao processar pagamento");
        }



    }
}
