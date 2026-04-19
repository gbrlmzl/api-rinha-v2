package rinhacampusiv.api.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PaymentExpirationJob {

    @Autowired
    private TeamRepository teamRepository;

    @Scheduled(fixedRate = 120 * 1000) // a cada 5 minutos
    public void checkExpiredPayments() {
        System.out.println("Verificando pagamentos expirados...");
        List<Team> teams = teamRepository.findAllPendingPayments();
        System.out.println("Encontrados " + teams.size() + " equipes com pagamentos pendentes.");
        teams.forEach(team -> {
            team.getPayments().stream()
                    .filter(p -> p.isPending() && p.getExpiresAt().isBefore(OffsetDateTime.now()))
                    .forEach(p -> {
                        p.setStatus("expired");
                        team.setStatus(TeamStatus.CANCELED); // ou outro status
                    });
            teamRepository.save(team);
        });
    }
}

