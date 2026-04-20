package rinhacampusiv.api.v2.service.tournament.payment;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.infra.exception.payments.PaymentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.payments.TeamWithoutPaymentException;
import rinhacampusiv.api.v2.service.email.EmailService;

import java.util.Map;

@Service
public class PaymentConfirmationService {

    @Autowired
    private SimpMessagingTemplate messageSender;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmailService emailService;

    public void verifyPayment(Payment paymentData) {
        String mercadoPagoPaymentId = String.valueOf(paymentData.getId());
        PaymentEntity payment = paymentRepository
                .findByMercadoPagoId(mercadoPagoPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado"));

        if (paymentData.getStatus().equalsIgnoreCase("approved") &&
                paymentData.getStatusDetail().equalsIgnoreCase("accredited")) {

            payment.approve(paymentData.getDateApproved(), paymentData.getStatusDetail());

            Team paymentTeam = payment.getTeam();
            if (paymentTeam == null) {
                throw new TeamWithoutPaymentException("Equipe sem pagamento gerado");
            }
            paymentTeam.approvedPayment();

            paymentRepository.save(payment);
            teamRepository.save(paymentTeam);

            Map<String, String> payload = Map.of("status", payment.getStatus().name());
            messageSender.convertAndSend(
                    "/topic/payment/" + payment.getUuid(),
                    payload
            );

            emailService.sendPaymentConfirmationEmail(paymentTeam);

        } else if ("cancelled".equals(paymentData.getStatus())) {

            payment.expire();

            paymentRepository.save(payment);

            Map<String, String> payload = Map.of("status", payment.getStatus().name());
            messageSender.convertAndSend(
                    "/topic/payment/" + payment.getUuid(),
                    payload
            );
            System.out.println("[Webhook] Pagamento expirado: " + payment.getUuid());
        }

    }


}

