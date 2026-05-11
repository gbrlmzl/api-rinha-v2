package rinhacampusiv.api.v2.infra.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;

@Component
public class TournamentSlugInitializer implements ApplicationRunner {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        tournamentRepository.findAll().stream()
                .filter(t -> t.getSlug() == null || t.getSlug().isBlank())
                .forEach(tournamentRepository::save); // @PreUpdate gera o slug automaticamente
    }
}
