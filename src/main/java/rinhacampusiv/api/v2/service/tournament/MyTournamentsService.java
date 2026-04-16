package rinhacampusiv.api.v2.service.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentMapper;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;

@Service
public class MyTournamentsService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Transactional(readOnly = true)
    public Page<TournamentPublicSummaryData> getMyActiveTournaments(TournamentGame game, Pageable pageable, String userEmail) {
        return teamRepository.findTeamsByUserEmailAndGame(userEmail, game, pageable)
                .map(Team::getTournament)
                .map(t -> tournamentMapper.toPublicSummaryData(t, countTeams(t.getId())));
    }

    private long countTeams(Long tournamentId) {
        return teamRepository.countByTournamentIdAndStatus(tournamentId, TeamStatus.READY);
    }
}
