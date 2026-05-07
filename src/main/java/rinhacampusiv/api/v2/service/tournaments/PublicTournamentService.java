package rinhacampusiv.api.v2.service.tournaments;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamPublicData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.UserTeamStatusData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;
import rinhacampusiv.api.v2.utils.tournaments.TeamCountUtils;

import java.util.List;
import java.util.Map;

@Service
public class PublicTournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    //Implementar a verificação para a seção de torneios FINISHED
    @Transactional(readOnly = true)
    public TournamentPublicDetailData getPublicTournamentView(Long id, Long userId) {
        return buildPublicDetail(tournamentRepository.findByIdOrThrow(id), userId);
    }

    @Transactional(readOnly = true)
    public TournamentPublicDetailData getPublicTournamentViewBySlug(String slug, Long userId) {
        return buildPublicDetail(tournamentRepository.findBySlugOrThrow(slug), userId);
    }

    @Transactional(readOnly = true)
    public Page<TournamentPublicSummaryData> listByGameAndStatusIn(TournamentGame game, List<TournamentStatus> statuses, Pageable pageable) {
        Page<Tournament> page = tournamentRepository.findByGameAndStatusIn(game, statuses, pageable);
        List<Long> ids = page.map(Tournament::getId).toList();
        Map<Long, Integer> counts = TeamCountUtils.toCountMap(
                teamRepository.countByTournamentIdsAndStatus(ids, TeamStatus.READY));

        return page.map(t -> new TournamentPublicSummaryData(t, counts.getOrDefault(t.getId(), 0)));
    }

    private TournamentPublicDetailData buildPublicDetail(Tournament tournament, Long userId) {
        if (tournament.getStatus() == TournamentStatus.CANCELED) {
            throw new EntityNotFoundException("Torneio não disponível");
        }

        Long id = tournament.getId();
        List<Team> readyTeams = teamRepository.findReadyTeamsWithDetails(id);

        List<TeamPublicData> confirmedTeams = readyTeams.stream()
                .map(TeamPublicData::new)
                .toList();

        UserTeamStatusData userTeam = userId == null ? null : teamRepository
                .findByCaptainIdAndTournamentIdAndStatusNot(userId, id, TeamStatus.CANCELED)
                .map(UserTeamStatusData::new)
                .orElse(null);

        return new TournamentPublicDetailData(tournament, readyTeams.size(), confirmedTeams, userTeam);
    }
}
