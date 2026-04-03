package rinhacampusiv.api.v2.service;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.websocket.WebSocketPaymentData;
import rinhacampusiv.api.v2.infra.exception.payments.PaymentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.payments.TeamWithoutPaymentException;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VerifyEfetuedPaymentService {

    @Autowired
    private SimpMessagingTemplate messageSender;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    TeamRepository teamRepository;

    public void verifyPayment(Payment paymentData) {
        String mercadoPagoPaymentId = String.valueOf(paymentData.getId());

        PaymentEntity payment = paymentRepository
                .findByMercadoPagoId(mercadoPagoPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado"));

        if (paymentData.getStatus().equalsIgnoreCase("approved") &&
                paymentData.getStatusDetail().equalsIgnoreCase("accredited")) {

            payment.setStatus("APPROVED");
            payment.setStatusDetail("PAGAMENTO REALIZADO");
            payment.setPaidAt(paymentData.getDateApproved());

            Team paymentTeam = payment.getTeam();
            if (paymentTeam == null) {
                throw new TeamWithoutPaymentException("Equipe sem pagamento gerado");
            }
            paymentTeam.approvedPayment();

            paymentRepository.save(payment);
            teamRepository.save(paymentTeam);

            WebSocketPaymentData webSocketMessageData = new WebSocketPaymentData(
                    payment, "Pagamento confirmado com sucesso"
            );

            messageSender.convertAndSend(
                    "/topic/pagamentos/" + webSocketMessageData.uuid(),
                    webSocketMessageData
            );

        } else {
            messageSender.convertAndSend(
                    "/topic/pagamentos/" + payment.getUuid(),
                    "Pagamento " + payment.getMercadoPagoId() + " ainda não aprovado: "
                            + payment.getStatus()
                            + " (" + payment.getStatusDetail() + ")"
            );
        }
    }

}

