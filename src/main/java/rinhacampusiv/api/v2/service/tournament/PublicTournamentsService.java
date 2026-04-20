package rinhacampusiv.api.v2.service.tournament;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;
import rinhacampusiv.api.v2.infra.exception.TournamentNotFoundException;

import java.util.List;

@Service
public class PublicTournamentsService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;


    //Implementar a verificação para a seção de torneios FINISHED
    @Transactional(readOnly = true)
    public TournamentPublicDetailData getPublicTournamentView(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

        if (tournament.getStatus() == TournamentStatus.CANCELED) {
            throw new EntityNotFoundException("Torneio não disponível");
        }

        Integer confirmedTeams = countTeams(tournament);
        return new TournamentPublicDetailData(tournament, confirmedTeams);
    }


    private Integer countTeams(Tournament t) {
        return teamRepository.countByTournamentIdAndStatus(t.getId(), TeamStatus.READY);
    }

    @Transactional(readOnly = true)
    public Page<TournamentPublicSummaryData> listByGameAndStatusIn(TournamentGame game, List<TournamentStatus> statuses, Pageable pageable) {
        return tournamentRepository.findByGameAndStatusIn(game, statuses, pageable)
                .map(t -> new TournamentPublicSummaryData(t, countTeams(t)));
    }
}
