package rinhacampusiv.api.v2.service.tournament.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PaymentExpirationJob {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private PaymentEventService paymentEventService;

    @Scheduled(fixedRate = 120 * 1000) // a cada 2 minutos
    public void checkExpiredPayments() {
        System.out.println("Verificando pagamentos expirados...");
        List<Team> teams = teamRepository.findAllPendingPayments();
        System.out.println("Encontrados " + teams.size() + " equipes com pagamentos pendentes.");
        teams.forEach(team -> {
            team.getPayments().stream()
                    .filter(p -> p.isPending() && p.getExpiresAt().isBefore(OffsetDateTime.now()))
                    .forEach(p -> {
                        //await chamada para a API do mercadopago para cancelar o pedido
                        boolean response = mercadoPagoClient.cancelPayment(p.getMercadoPagoId());

                        if(response){
                            p.expire();
                            team.expiredPayment();
                        }else{
                            p.expire();
                            team.expiredPaymentProblem();
                        }
                        paymentEventService.save(p, PaymentEventType.EXPIRED_BY_JOB);

                    });
            teamRepository.save(team);
        });
    }
}
