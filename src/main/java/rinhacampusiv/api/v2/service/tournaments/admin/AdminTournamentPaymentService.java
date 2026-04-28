package rinhacampusiv.api.v2.service.tournaments.admin;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.*;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.infra.exception.tournaments.TeamNotFoundException;
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminTournamentPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventRepository eventRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Page<PaymentEventData> listPayments(Long tournamentId, PaymentStatus status, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new TournamentNotFoundException("Torneio não encontrado");
        }

        Page<PaymentEntity> paymentsPage = paymentRepository.findByTournamentIdAndStatus(tournamentId, status, pageable);

        return mapToPaymentEventDataPage(paymentsPage);
    }

    @Transactional(readOnly = true)
    public List<PaymentEventResponseData> getEvents(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }
        return eventRepository.findByPaymentId(paymentId).stream().map(PaymentEventResponseData :: new).toList();
    }

    @Transactional(readOnly = true)
    public Page<PaymentEventData> listTeamPayments(Long tournamentId, Long teamId, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new TournamentNotFoundException("Torneio não encontrado");
        }
        if (!teamRepository.existsById(teamId)) {
            throw new TeamNotFoundException("Equipe não encontrada");
        }

        Page<PaymentEntity> paymentsPage = paymentRepository.findByTeamIdAndTournamentId(teamId, tournamentId, pageable);

        return mapToPaymentEventDataPage(paymentsPage);
    }

    // AUXILIARES

    private Page<PaymentEventData> mapToPaymentEventDataPage(Page<PaymentEntity> paymentsPage) {
        List<Long> paymentIds = paymentsPage.getContent().stream()
                .map(PaymentEntity::getId)
                .toList();

        if (paymentIds.isEmpty()) {
            return paymentsPage.map(payment -> new PaymentEventData(payment, null));
        }

        Map<Long, PaymentEventType> lastEventMap = eventRepository.findLastEventsByPaymentIds(paymentIds)
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getPayment().getId(),
                        PaymentEvent::getEventType
                ));

        return paymentsPage.map(payment ->
                new PaymentEventData(payment, lastEventMap.get(payment.getId()))
        );
    }
}
