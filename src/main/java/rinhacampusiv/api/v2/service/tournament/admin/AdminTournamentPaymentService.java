package rinhacampusiv.api.v2.service.tournament.admin;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventData;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;

import java.util.List;

@Service
public class AdminTournamentPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventRepository eventRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional(readOnly = true)
    public Page<PaymentEventData> listPayments(Long tournamentId, PaymentStatus status, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new EntityNotFoundException("Torneio não encontrado");
        }

        return paymentRepository.findByTournamentIdAndStatus(tournamentId, status, pageable)
                .map(payment -> {
                    boolean hasEvent = !eventRepository.findByPaymentId(payment.getId()).isEmpty();
                    return new PaymentEventData(payment, hasEvent);
                });
    }

    @Transactional(readOnly = true)
    public List<PaymentEvent> getEvents(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }
        return eventRepository.findByPaymentId(paymentId);
    }
}
