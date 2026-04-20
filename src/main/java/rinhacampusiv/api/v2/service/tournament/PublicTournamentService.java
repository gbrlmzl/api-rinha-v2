package rinhacampusiv.api.v2.service.tournament;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.UserTeamStatusData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.PlayerPublicData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TeamPublicData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;
import rinhacampusiv.api.v2.infra.exception.TournamentNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicTournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;


        //Implementar a verificação para a seção de torneios FINISHED
        @Transactional(readOnly = true)
        public TournamentPublicDetailData getPublicTournamentView(Long id, Long userId) {
            Tournament tournament = tournamentRepository.findById(id)
                    .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

            if (tournament.getStatus() == TournamentStatus.CANCELED)
                throw new EntityNotFoundException("Torneio não disponível");

            List<Team> readyTeams = teamRepository.findReadyTeamsWithDetails(id);

            List<TeamPublicData> confirmedTeams = readyTeams.stream()
                    .map(TeamPublicData::new)
                    .toList();

            UserTeamStatusData userTeam = null;
            if (userId != null)
                userTeam = teamRepository
                        .findByCaptainIdAndTournamentIdAndStatusNot(userId, id, TeamStatus.CANCELED)
                        .map(UserTeamStatusData::new)
                        .orElse(null);

            return new TournamentPublicDetailData(tournament, readyTeams.size(), confirmedTeams, userTeam);
        }

        @Transactional(readOnly = true)
        public Page<TournamentPublicSummaryData> listByGameAndStatusIn(TournamentGame game, List<TournamentStatus> statuses, Pageable pageable) {
            Page<Tournament> page = tournamentRepository.findByGameAndStatusIn(game, statuses, pageable);
            List<Long> ids = page.map(Tournament::getId).toList();
            Map<Long, Long> counts = teamRepository.countByTournamentIdsAndStatus(ids, TeamStatus.READY)
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (Long) row[0],
                            row -> (Long) row[1]
                    ));

            return page.map(t -> new TournamentPublicSummaryData(t,
                    counts.getOrDefault(t.getId(), 0L).intValue()));
        }
    }
