package rinhacampusiv.api.v2.service.tournaments.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;

@Service
public class PaymentCancellationService {

    private static final Logger log = LoggerFactory.getLogger(PaymentCancellationService.class);

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private PaymentEventRepository eventRepository;

    public void cancelTeamPayments(Team team, String logContext) {
        for (PaymentEntity payment : team.getPayments()) {
            if (payment.isCanceled()) continue;

            if (payment.isPending()) {
                boolean canceled = mercadoPagoClient.cancelPayment(payment.getMercadoPagoId(), payment.getId());
                if (!canceled) {
                    log.warn("[{}] Falha ao cancelar pagamento no MP — paymentId: {}, mpId: {}",
                            logContext, payment.getId(), payment.getMercadoPagoId());
                }
            }

            if (payment.isApproved()) {
                log.warn("[{}] Pagamento APROVADO cancelado por admin — paymentId: {}, valor: {}, payer: {}",
                        logContext, payment.getId(), payment.getValue(), payment.getPayer());
            }

            payment.cancelByAdmin();
            eventRepository.save(new PaymentEvent(payment, PaymentEventType.CANCELED_BY_ADMIN));
        }
    }
}
