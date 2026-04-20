package rinhacampusiv.api.v2.service.tournament.payment;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.infra.external.mercadopago.WebHookNotificationData;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;

@Service
public class PaymentWebhookService {

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
            saveEvent(null, null, PaymentEventType.IGNORED, null, null,"body ausente ou sem data");
            return;
        }

        String paymentId = body.data().id();
        PaymentEntity paymentEntity = paymentRepository
                .findByMercadoPagoId(paymentId)
                .orElse(null);

        Payment mpPayment = mercadoPagoClient.findPayment(paymentId, true);
        if (mpPayment == null){
            saveEvent(paymentEntity, paymentId, PaymentEventType.IGNORED, null, null, "payment not found");
            return;
        }

        String statusFromMp = mpPayment.getStatus();
        String statusDetailFromMp = mpPayment.getStatusDetail();

        if (!"approved".equals(statusFromMp)) {
            saveEvent(paymentEntity, paymentId, PaymentEventType.IGNORED, statusFromMp, null, null);
            return;
        }

        try {
            paymentConfirmationService.verifyPayment(mpPayment);
            saveEvent(paymentEntity, paymentId, PaymentEventType.PROCESSED, statusFromMp,statusDetailFromMp, null);
        } catch (Exception e) {
            saveEvent(paymentEntity, paymentId, PaymentEventType.ERROR, statusFromMp,statusDetailFromMp, e.getMessage());
        }
    }

    private void saveEvent(PaymentEntity payment, String mpId, PaymentEventType type,
                           String statusFromMp, String statusDetailFromMp, String error) {
        PaymentEvent event = new PaymentEvent();
        event.setPayment(payment);
        event.setMercadoPagoId(mpId);
        event.setEventType(type);
        event.setStatusFromMp(statusFromMp);
        event.setStatusDetailFromMp(statusDetailFromMp);
        event.setErrorMessage(error);
        eventRepository.save(event);
    }
}