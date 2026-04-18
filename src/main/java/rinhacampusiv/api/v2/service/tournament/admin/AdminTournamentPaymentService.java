package rinhacampusiv.api.v2.service.tournament.admin;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.*;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;

import java.util.List;

@Service
public class AdminTournamentPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentWebhookLogRepository webhookLogRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional(readOnly = true)
    public Page<PaymentLogData> listPayments(Long tournamentId, PaymentStatus status, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new EntityNotFoundException("Torneio não encontrado");
        }

        return paymentRepository.findByTournamentIdAndStatus(tournamentId, status, pageable)
                .map(payment -> {
                    boolean hasLog = !webhookLogRepository.findByPaymentId(payment.getId()).isEmpty();
                    return new PaymentLogData(payment, hasLog);
                });
    }

    @Transactional(readOnly = true)
    public List<PaymentWebhookLog> getRawLogs(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }
        return webhookLogRepository.findByPaymentId(paymentId);
    }
}
