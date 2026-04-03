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

    @Value("${mercadopago.test.token}")
    private String accessTokenTest;

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
                .description("Inscrição Rinha do Campus IV")
                .paymentMethodId("pix")
                .dateOfExpiration(OffsetDateTime.now().plusHours(1))
                //.notificationUrl("//NotificationUrl")
                .payer(payerData)
                .build();

        try {
            //pagamento - Payment
            return client.create(paymentData, requestOptions);
        } catch (MPApiException e) {
            throw new MercadoPagoPaymentException(
                    "Erro ao criar pagamento no Mercado Pago",
                    e.getApiResponse().getStatusCode(),
                    "Detalhes: " + e.getApiResponse().getContent()
            );
        } catch (MPException e) {
            throw new RuntimeException("Erro inesperado ao processar pagamento: " + e.getMessage());
        }



    }
}
