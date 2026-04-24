package rinhacampusiv.api.v2.service.tournaments.payment;

import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.MercadoPagoEventData;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.infra.external.mercadopago.WebHookNotificationData;

@Service
public class PaymentWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private PaymentConfirmationService paymentConfirmationService;

    @Autowired
    private PaymentEventRepository eventRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public void processPayment(WebHookNotificationData body) {
        if (body == null || body.data() == null) {
            log.warn("[WEBHOOK] Notificação recebida sem dados — ignorada");
            eventRepository.save(new PaymentEvent(null, PaymentEventType.IGNORED,
                    MercadoPagoEventData.ignored(null, "Corpo da requisição ausente ou sem dados")));
            return;
        }

        String paymentId = body.data().id();
        log.info("[WEBHOOK] Notificação recebida | mpId={}", paymentId);

        PaymentEntity paymentEntity = paymentRepository.findByMercadoPagoId(paymentId).orElse(null);

        String payer = paymentEntity != null ? paymentEntity.getPayer() : "N/A";
        Payment mpPayment = mercadoPagoClient.findPayment(paymentId, true, payer);
        if (mpPayment == null) {
            log.warn("[WEBHOOK] Pagamento não encontrado no MP — ignorado | mpId={}", paymentId);
            eventRepository.save(new PaymentEvent(paymentEntity, PaymentEventType.IGNORED,
                    MercadoPagoEventData.ignored(paymentId, "payment not found")));
            return;
        }

        String statusFromMp = mpPayment.getStatus();
        String statusDetailFromMp = mpPayment.getStatusDetail();

        if (!"approved".equals(statusFromMp)) {
            log.info("[WEBHOOK] Status não processável — ignorado | mpId={} | status={}", paymentId, statusFromMp);
            eventRepository.save(new PaymentEvent(paymentEntity, PaymentEventType.IGNORED,
                    MercadoPagoEventData.ignoredWithStatus(paymentId, statusFromMp)));
            return;
        }

        try {
            paymentConfirmationService.verifyPayment(mpPayment);
            eventRepository.save(new PaymentEvent(paymentEntity, PaymentEventType.PROCESSED,
                    MercadoPagoEventData.processed(paymentId, statusFromMp, statusDetailFromMp)));
        } catch (Exception e) {
            log.error("[WEBHOOK] Erro ao processar pagamento aprovado | mpId={} | erro={}", paymentId, e.getMessage());
            eventRepository.save(new PaymentEvent(paymentEntity, PaymentEventType.ERROR,
                    MercadoPagoEventData.error(paymentId, statusFromMp, statusDetailFromMp, e.getMessage())));
        }
    }
}
