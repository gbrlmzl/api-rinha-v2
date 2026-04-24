package rinhacampusiv.api.v2.service.tournaments.payment;

import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PaymentConfirmationService.class);

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

            log.info("[PAYMENT] Pagamento aprovado | uuid={} | equipe={} | torneio={}",
                    payment.getUuid(), paymentTeam.getName(), paymentTeam.getTournament().getName());

            Map<String, String> payload = Map.of("status", payment.getStatus().name());
            messageSender.convertAndSend("/topic/payment/" + payment.getUuid(), payload);

            emailService.sendPaymentConfirmationEmail(paymentTeam);

        } else if ("cancelled".equals(paymentData.getStatus())) {

            payment.expire();
            paymentRepository.save(payment);

            log.warn("[PAYMENT] Pagamento expirado via webhook | uuid={} | mpId={}",
                    payment.getUuid(), mercadoPagoPaymentId);

            Map<String, String> payload = Map.of("status", payment.getStatus().name());
            messageSender.convertAndSend("/topic/payment/" + payment.getUuid(), payload);
        }
    }
}
