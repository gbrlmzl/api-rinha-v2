package rinhacampusiv.api.v2.service.tournament.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
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
import rinhacampusiv.api.v2.infra.exception.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.infra.external.ImgurClient;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.service.tournament.payment.PaymentEventService;
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
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private ImgurClient imgurClient;

    @Autowired
    private PaymentEventService paymentEventService;

    @Transactional
    public TournamentAdminDetailData createTournament(TournamentCreationData tournamentData, MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new ValidatorException("A imagem do torneio é obrigatória.");
        }

        creationValidators.forEach(v -> v.validar(tournamentData));

        String imageUrl = imgurClient.uploadTournamentImage(image, tournamentData.name());
        Tournament tournament = new Tournament(tournamentData);
        tournament.setImageUrl(imageUrl);

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

        return tournamentsPage.map(tournament ->
                new TournamentAdminSummaryData(tournament,
                        confirmedTeamsMap.getOrDefault(tournament.getId(), 0))
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
            } else if (data.maxTeams() > activeTeams && currentStatus == TournamentStatus.FULL) {
                tournament.setStatus(TournamentStatus.OPEN);
            }
        }

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
            log.warn("Torneio deletado — ID: {}, Nome: {}, Jogo: {}",
                    tournament.getId(), tournament.getName(), tournament.getGame());
            tournamentRepository.delete(tournament);
            return;
        }

        // force == true com equipes — cancela com cascade
        List<Team> teams = teamRepository.findAllByTournamentIdWithDetails(id);
        cancelPayments(teams); //Cancela o pagamento das equipes que existem.

        tournament.setStatus(TournamentStatus.CANCELED);

        log.warn("Torneio cancelado — ID: {}, Nome: {}, Jogo: {}, Equipes afetadas: {}",
                tournament.getId(), tournament.getName(), tournament.getGame(), totalTeams);
    }

    private void cancelPayments(List<Team> teams) {
        for (Team team : teams) {
            for (PaymentEntity payment : team.getPayments()) {
                if (payment.isCanceled()) continue;

                if (payment.isPending()) {
                    boolean canceled = mercadoPagoClient.cancelPayment(payment.getMercadoPagoId());
                    if (!canceled) {
                        log.warn("Falha ao cancelar pagamento no MP — paymentId: {}, mpId: {}",
                                payment.getId(), payment.getMercadoPagoId());
                    }
                }

                if (payment.isApproved()) {
                    log.warn("Pagamento APROVADO cancelado por admin — paymentId: {}, valor: {}, payer: {}",
                            payment.getId(), payment.getValue(), payment.getPayer());
                }

                payment.cancelByAdmin();
                paymentEventService.save(payment, PaymentEventType.CANCELED_BY_ADMIN);
            }

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
