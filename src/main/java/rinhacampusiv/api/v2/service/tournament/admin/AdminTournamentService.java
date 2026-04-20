package rinhacampusiv.api.v2.service.tournament.admin;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.validators.tournament.creation.TournamentCreationValidator;
import rinhacampusiv.api.v2.validators.tournament.update.TournamentUpdateValidator;

import java.util.List;

@Service
public class AdminTournamentService {

    private static final Logger log = LoggerFactory.getLogger(AdminTournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private List<TournamentCreationValidator> creationValidators;

    @Autowired
    private List<TournamentUpdateValidator> updateValidators;

    @Transactional
    public TournamentAdminDetailData createTournament(TournamentCreationData tournamentData) {

        creationValidators.forEach(creationValidator -> creationValidator.validar(tournamentData));

        Tournament tournament = new Tournament(tournamentData);
        tournamentRepository.save(tournament);

        return new TournamentAdminDetailData(tournament);
    }


    @Transactional(readOnly = true)
    public Page<TournamentAdminSummaryData> getAllTournaments(TournamentGame game, Pageable pageable) {
        Page<Tournament> tournamentsPage;

        if (game != null) {
            tournamentsPage = tournamentRepository.findByGame(game, pageable);
        } else {
            tournamentsPage = tournamentRepository.findAll(pageable);
        }

        return tournamentsPage.map(tournament -> {
            Integer confirmedTeams = teamRepository.countByTournamentIdAndStatus(tournament.getId(), TeamStatus.READY);
            return new TournamentAdminSummaryData(tournament, confirmedTeams);
        });
    }

    @Transactional(readOnly = true)
    public TournamentAdminDetailData getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

        return new TournamentAdminDetailData(tournament);
    }


    @Transactional
    public TournamentAdminDetailData updateTournament(Long id, TournamentUpdateData data) {

        Tournament tournament = findTournamentById(id);

        updateValidators.forEach(v -> v.validar(tournament, data));
        tournament.updateInformation(data);
        return new TournamentAdminDetailData(tournament);
    }

    @Transactional
    public void cancelTournament(Long id, boolean force) {
        Tournament tournament = findTournamentById(id);

        Integer totalTeams = teamRepository.countByTournamentId(id);

        // Se tem equipes e a confirmação NÃO foi enviada, bloqueia e avisa
        if (totalTeams > 0 && !force) {
            throw new ValidatorException("O torneio possui " + totalTeams + " equipe(s) vinculada(s). Deseja realmente cancelar o torneio?");
        }

        // Se tem equipes e a confirmação FOI enviada, apaga as dependências primeiro
        if (totalTeams > 0 && force) {
            tournament.setStatus(TournamentStatus.CANCELED);
        }

        log.warn("Excluído Torneio — ID: {}, Nome: {}, Jogo: {}",
                tournament.getId(), tournament.getName(), tournament.getGame());
        tournamentRepository.delete(tournament);
    }

    //AUXILIARES

    private Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }
}
