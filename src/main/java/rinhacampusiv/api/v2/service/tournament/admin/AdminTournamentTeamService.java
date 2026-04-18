package rinhacampusiv.api.v2.service.tournament.admin;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

@Service
public class AdminTournamentTeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional(readOnly = true)
    public Page<TeamAdminSummaryData> listTeams(Long tournamentId, Pageable pageable) {

        findTournamentById(tournamentId);

        return teamRepository.findByTournamentId(tournamentId, pageable)
                .map(team -> {
                    PaymentStatus lastStatus = team.getPayments().isEmpty()
                            ? null
                            : team.getPayments().getLast().getStatus();

                    return new TeamAdminSummaryData(team);
                });
    }

    @Transactional
    public void banTeam(Long tournamentId, Long teamId) {
        Tournament tournament = findTournamentById(tournamentId);

        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            throw new ValidatorException("Não é possível banir equipes de um torneio encerrado.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        if (!team.getTournament().getId().equals(tournamentId)) {
            throw new EntityNotFoundException("Equipe não pertence a este torneio");
        }

        boolean teamWasActive = team.isActive();

        team.ban();

        if (teamWasActive && tournament.getStatus() == TournamentStatus.FULL) {
            tournament.setStatus(TournamentStatus.OPEN);
            tournamentRepository.save(tournament);
        }
    }

    //AUXILIARES

    private Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));
    }

}
