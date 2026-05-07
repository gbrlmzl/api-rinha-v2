package rinhacampusiv.api.v2.service.tournaments.payment;

import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

    @Transactional
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

            notifyPaymentStatusAfterCommit(payment);

            emailService.sendPaymentConfirmationEmail(paymentTeam);

        } else if ("cancelled".equals(paymentData.getStatus())) {

            payment.expire();
            paymentRepository.save(payment);

            log.warn("[PAYMENT] Pagamento expirado via webhook | uuid={} | mpId={}",
                    payment.getUuid(), mercadoPagoPaymentId);

            notifyPaymentStatusAfterCommit(payment);
        }
    }

    /**
     * Agenda o envio da mensagem STOMP para depois do commit da transação atual.
     *
     * Sem isso, o front receberia o WS antes das mudanças estarem visíveis no banco
     * e veria dados antigos ao re-buscar o status.
     */
    private void notifyPaymentStatusAfterCommit(PaymentEntity payment) {
        String uuid = payment.getUuid();
        String statusName = payment.getStatus().name();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Map<String, String> payload = Map.of("status", statusName);
                messageSender.convertAndSend("/topic/payment/" + uuid, payload);
            }
        });
    }
}
