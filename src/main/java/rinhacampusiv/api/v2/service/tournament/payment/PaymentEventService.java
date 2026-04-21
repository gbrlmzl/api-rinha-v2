package rinhacampusiv.api.v2.service.tournament.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;

@Service
public class PaymentEventService {

    @Autowired
    private PaymentEventRepository eventRepository;

    public void save(PaymentEntity payment, PaymentEventType type) {
        save(payment, null, type, null, null, null);
    }

    public void save(PaymentEntity payment, String mpId, PaymentEventType type,
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
