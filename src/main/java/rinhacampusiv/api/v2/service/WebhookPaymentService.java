package rinhacampusiv.api.v2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.mercadoPago.MercadoPagoService;
import rinhacampusiv.api.v2.domain.mercadoPago.WebHookNotificationData;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentWebhookLog;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentWebhookLogRepository;


import java.util.Map;

@Service
public class WebhookPaymentService {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private VerifyEfetuedPaymentService verifyPaymentService;

    @Autowired
    private PaymentWebhookLogRepository webhookLogRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public void process(WebHookNotificationData body) {
        if (body == null || body.data() == null) {
            saveLog(null, null, "IGNORED", "body ausente ou sem data");
            return;
        }

        String paymentId = body.data().id();

        Payment mpPayment = mercadoPagoService.findPayment(paymentId, true);

        if (mpPayment == null) {
            saveLog(null, paymentId, "IGNORED", "não encontrado no MP");
            return;
        }

        if ("approved".equals(mpPayment.getStatus())) {
            try {
                verifyPaymentService.verifyPayment(mpPayment);
                PaymentEntity paymentEntity = paymentRepository
                        .findByMercadoPagoId(String.valueOf(mpPayment.getId()))
                        .orElse(null);
                saveLog(paymentEntity, paymentId, "PROCESSED", null);
            } catch (Exception e) {
                saveLog(null, paymentId, "ERROR", e.getMessage());
            }
        } else {
            saveLog(null, paymentId, "IGNORED", "status: " + mpPayment.getStatus());
        }
    }

    private void saveLog(PaymentEntity payment, String mpId, String outcome, String error) {
        PaymentWebhookLog log = new PaymentWebhookLog();
        log.setPayment(payment);
        log.setMercadoPagoId(mpId);
        log.setRawPayload("{}");
        log.setProcessingOutcome(outcome);
        log.setErrorMessage(error);
        webhookLogRepository.save(log);
    }
}