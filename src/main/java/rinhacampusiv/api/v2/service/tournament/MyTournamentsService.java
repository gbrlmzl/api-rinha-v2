package rinhacampusiv.api.v2.service.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.MyTournamentsSummaryData;

@Service
public class MyTournamentsService {

    @Autowired
    private TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Page<MyTournamentsSummaryData> getMyActiveTournaments(TournamentGame game, Pageable pageable, String userEmail) {
        return teamRepository.findTeamsByUserEmailAndGame(userEmail, game, pageable)
                .map(team -> new MyTournamentsSummaryData(team.getTournament(), team));
    }
}
