package rinhacampusiv.api.v2.service.tournament.admin;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.infra.exception.TeamNotFoundException;
import rinhacampusiv.api.v2.infra.exception.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.validators.tournament.team.ban.TournamentTeamBanValidator;

import java.util.List;

@Service
public class AdminTournamentTeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    List<TournamentTeamBanValidator> tournamentTeamBanValidators;

    @Transactional(readOnly = true)
    public Page<TeamAdminSummaryData> listTeams(Long tournamentId, List<TeamStatus> statusList, Pageable pageable) {

        findTournamentById(tournamentId);

        return teamRepository.findByTournamentIdAndStatusIn(tournamentId, statusList, pageable)
                .map(TeamAdminSummaryData::new);
        }


    @Transactional
    public void banTeam(Long tournamentId, Long teamId) {
        Tournament tournament = findTournamentById(tournamentId);

        tournamentTeamBanValidators.forEach(validator -> validator.validar(tournament, teamId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Equipe não encontrada"));

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
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }

}

