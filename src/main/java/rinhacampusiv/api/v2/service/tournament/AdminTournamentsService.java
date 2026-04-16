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
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentMapper;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.validators.tournament.TournamentCreation.TournamentCreationValidator;
import rinhacampusiv.api.v2.validators.tournament.TournamentUpdate.TournamentUpdateValidator;

import java.util.List;

@Service
public class AdminTournamentsService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private List<TournamentCreationValidator> creationValidators;

    @Autowired
    private List<TournamentUpdateValidator> updateValidators;

    @Transactional
    public TournamentAdminDetailData createTournament(TournamentCreationData tournamentData) {

        creationValidators.forEach(creationValidator -> creationValidator.validar(tournamentData));

        Tournament tournament = new Tournament(tournamentData);
        tournamentRepository.save(tournament);

        return tournamentMapper.toAdminDetailData(tournament);
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
            long confirmedTeams = teamRepository.countByTournamentIdAndStatus(tournament.getId(), TeamStatus.READY);
            return tournamentMapper.toAdminSummaryData(tournament, confirmedTeams);
        });
    }

    @Transactional(readOnly = true)
    public TournamentAdminDetailData getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));

        return tournamentMapper.toAdminDetailData(tournament);
    }


    @Transactional
    public TournamentAdminDetailData updateTournament(Long id, TournamentUpdateData data) {

        Tournament tournament = findTournamentById(id);

        updateValidators.forEach(v -> v.validar(tournament, data));
        tournament.updateInformation(data);
        return tournamentMapper.toAdminDetailData(tournament);
    }

    @Transactional
    public void deleteTournament(Long id, boolean force) {
        Tournament tournament = findTournamentById(id);

        long totalTeams = teamRepository.countByTournamentId(id);

        // Se tem equipes e a confirmação NÃO foi enviada, bloqueia e avisa
        if (totalTeams > 0 && !force) {
            throw new ValidatorException("O torneio possui " + totalTeams + " equipe(s) vinculada(s). Excluir o torneio apagará todas as equipes. Envie a confirmação (force=true) se tiver certeza.");
        }

        // Se tem equipes e a confirmação FOI enviada, apaga as dependências primeiro
        if (totalTeams > 0 && force) {
            teamRepository.deleteAllByTournamentId(id);
            // TODO: Se existirem pagamentos atrelados aos times, eles devem ser apagados aqui
        }

        tournamentRepository.delete(tournament);
    }


    //AUXILIARES

    private Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));
    }
}
