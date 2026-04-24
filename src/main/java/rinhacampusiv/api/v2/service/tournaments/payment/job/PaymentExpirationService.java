package rinhacampusiv.api.v2.service.tournaments.payment.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Contém a lógica transacional do job de expiração de pagamentos.
 *
 * Separada do PaymentExpirationJob porque o Spring não consegue aplicar
 * @Transactional em métodos @Scheduled dentro da mesma classe — o proxy
 * transacional precisa ser invocado externamente para funcionar.
 */
@Service
public class PaymentExpirationService {

    private static final Logger log = LoggerFactory.getLogger(PaymentExpirationService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private PaymentEventRepository eventRepository;

    @Transactional
    public void processExpiredPayments() {
        List<Team> teams = teamRepository.findAllPendingPayments();

        if (teams.isEmpty()) {
            log.info("[JOB] Nenhum pagamento pendente encontrado");
            return;
        }

        log.info("[JOB] {} equipe(s) com pagamentos pendentes encontradas", teams.size());

        for (Team team : teams) {
            team.getPayments().stream()
                    .filter(p -> p.isPending() && p.getExpiresAt().isBefore(OffsetDateTime.now()))
                    .forEach(p -> {
                        log.info("[JOB] Expirando pagamento | paymentId={} | mpId={} | equipe={} | expiradoEm={}",
                                p.getId(), p.getMercadoPagoId(), team.getName(), p.getExpiresAt());

                        boolean canceled = mercadoPagoClient.cancelPayment(p.getMercadoPagoId(), p.getId());

                        if (canceled) {
                            p.expire();
                            team.expiredPayment();
                            log.info("[JOB] Pagamento expirado com sucesso | paymentId={} | equipe={}",
                                    p.getId(), team.getName());
                        } else {
                            p.expire();
                            team.expiredPaymentProblem();
                            log.warn("[JOB] Falha ao cancelar no MP, expiração registrada localmente | paymentId={} | mpId={} | equipe={}",
                                    p.getId(), p.getMercadoPagoId(), team.getName());
                        }

                        eventRepository.save(new PaymentEvent(p, PaymentEventType.EXPIRED_BY_JOB));
                    });

            teamRepository.save(team);
        }
    }
}
