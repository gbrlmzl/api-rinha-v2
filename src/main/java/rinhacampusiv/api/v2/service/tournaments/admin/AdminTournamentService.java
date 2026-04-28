package rinhacampusiv.api.v2.service.tournaments.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
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
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;
import rinhacampusiv.api.v2.infra.external.imgur.ImgurClient;
import rinhacampusiv.api.v2.service.tournaments.payment.PaymentCancellationService;
import rinhacampusiv.api.v2.validators.tournament.creation.TournamentCreationValidator;
import rinhacampusiv.api.v2.validators.tournament.update.TournamentUpdateValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private ImgurClient imgurClient;

    @Autowired
    private PaymentCancellationService paymentCancellationService;

    @Transactional
    public TournamentAdminDetailData createTournament(TournamentCreationData tournamentData, MultipartFile image) {
        log.info("[TORNEIO] Criando torneio | nome={} | jogo={} | maxEquipes={}",
                tournamentData.name(), tournamentData.game(), tournamentData.maxTeams());

        imgurClient.validateImage(image);
        creationValidators.forEach(v -> v.validar(tournamentData));

        String imageUrl = imgurClient.uploadTournamentImage(image, tournamentData.name());
        Tournament tournament = new Tournament(tournamentData);
        tournament.setImageUrl(imageUrl);

        tournamentRepository.save(tournament);

        log.info("[TORNEIO] Torneio criado com sucesso | id={} | nome={}", tournament.getId(), tournament.getName());
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

        List<Long> tournamentIds = tournamentsPage.getContent().stream()
                .map(Tournament::getId)
                .toList();

        Map<Long, Integer> confirmedTeamsMap = teamRepository
                .countByTournamentIdsAndStatus(tournamentIds, TeamStatus.READY)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        Map<Long, Integer> activeTeamsMap = teamRepository
                .countByTournamentIdsAndStatusIn(
                        tournamentIds,
                        List.of(TeamStatus.PENDING_PAYMENT, TeamStatus.READY))
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        return tournamentsPage.map(tournament ->
                new TournamentAdminSummaryData(tournament,
                        confirmedTeamsMap.getOrDefault(tournament.getId(), 0),
                        activeTeamsMap.getOrDefault(tournament.getId(), 0))
        );
    }

    @Transactional(readOnly = true)
    public TournamentAdminDetailData getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

        return new TournamentAdminDetailData(tournament);
    }

    @Transactional
    public TournamentAdminDetailData updateTournament(Long id, TournamentUpdateData data, MultipartFile image) {
        Tournament tournament = findTournamentById(id);

        log.info("[TORNEIO] Atualizando torneio | id={} | nome={}", id, tournament.getName());

        updateValidators.forEach(v -> v.validar(tournament, data));

        if (image != null && !image.isEmpty()) {
            String imageUrl = imgurClient.uploadTournamentImage(image, data.name() != null ? data.name() : tournament.getName());
            tournament.setImageUrl(imageUrl);
        }

        tournament.updateInformation(data);

        if (data.maxTeams() != null && data.status() == null) {
            Integer activeTeams = teamRepository.countByActiveTrueAndTournamentId(id);
            TournamentStatus currentStatus = tournament.getStatus();

            if (data.maxTeams().equals(activeTeams) && currentStatus == TournamentStatus.OPEN) {
                tournament.setStatus(TournamentStatus.FULL);
                log.info("[TORNEIO] Status atualizado para FULL | id={} | equipes={}", id, activeTeams);
            } else if (data.maxTeams() > activeTeams && currentStatus == TournamentStatus.FULL) {
                tournament.setStatus(TournamentStatus.OPEN);
                log.info("[TORNEIO] Status atualizado para OPEN | id={} | equipes={} | novoMax={}",
                        id, activeTeams, data.maxTeams());
            }
        }

        log.info("[TORNEIO] Torneio atualizado com sucesso | id={} | nome={}", id, tournament.getName());
        return new TournamentAdminDetailData(tournament);
    }

    @Transactional
    public void cancelTournament(Long id, boolean force) {
        Tournament tournament = findTournamentById(id);
        Integer totalTeams = teamRepository.countByTournamentId(id);

        if (totalTeams > 0 && !force) {
            throw new ValidatorException("O torneio possui " + totalTeams + " equipe(s) vinculada(s). Deseja realmente cancelar o torneio?");
        }

        if (totalTeams == 0) {
            log.warn("[TORNEIO] Torneio deletado — ID: {}, Nome: {}, Jogo: {}",
                    tournament.getId(), tournament.getName(), tournament.getGame());
            tournamentRepository.delete(tournament);
            return;
        }

        List<Team> teams = teamRepository.findAllByTournamentIdWithPayments(id);
        cancelPayments(teams);

        tournament.setStatus(TournamentStatus.CANCELED);

        log.warn("[TORNEIO] Torneio cancelado — ID: {}, Nome: {}, Jogo: {}, Equipes afetadas: {}",
                tournament.getId(), tournament.getName(), tournament.getGame(), totalTeams);
    }

    private void cancelPayments(List<Team> teams) {
        for (Team team : teams) {
            paymentCancellationService.cancelTeamPayments(team, "TORNEIO");
            team.cancelPayment();
            team.setActive(false);
        }
    }

    //AUXILIARES

    private Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }
}
