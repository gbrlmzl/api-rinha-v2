package rinhacampusiv.api.v2.service.tournament;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.*;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;

import java.util.List;

@Service
public class PublicTournamentsService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Transactional(readOnly = true)
    public TournamentPublicDetailData getPublicTournamentView(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));

        if (tournament.getStatus() == TournamentStatus.CANCELED) {
            throw new EntityNotFoundException("Torneio não disponível");
        }

        String currentUserEmail = getCurrentUserEmail();
        boolean isRegistered = false;

        if (currentUserEmail != null) {
            isRegistered = teamRepository.existsByTournamentIdAndUserEmail(id, currentUserEmail);
        }

        long confirmedTeams = countTeams(tournament);
        return tournamentMapper.toPublicDetailData(tournament, confirmedTeams, isRegistered);
    }

    private String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) return null;
        return auth.getName();
    }

    private long countTeams(Tournament t) {
        return teamRepository.countByTournamentIdAndStatus(t.getId(), TeamStatus.READY);
    }

    @Transactional(readOnly = true)
    public Page<TournamentPublicSummaryData> listByGameAndStatusIn(TournamentGame game, List<TournamentStatus> statuses, Pageable pageable) {
        return tournamentRepository.findByGameAndStatusIn(game, statuses, pageable)
                .map(t -> tournamentMapper.toPublicSummaryData(t, countTeams(t)));
    }
}

