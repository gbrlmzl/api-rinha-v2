package rinhacampusiv.api.v2.service.tournaments.registration;

import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventRepository;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventType;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.CancelRegistrationDto;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.CheckRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.CanceledTeamData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentRegistrationStatusData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.tournaments.TeamNotFoundException;
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentFullException;
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.auth.UserNotAuthenticatedException;
import rinhacampusiv.api.v2.infra.external.imgur.ImgurClient;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.validators.tournament.team.register.TournamentTeamRegisterValidator;
import rinhacampusiv.api.v2.validators.tournament.team.register.retry.TournamentRetryRegisterValidator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(TournamentRegistrationService.class);

    @Autowired
    private ImgurClient imgurClient;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private List<TournamentTeamRegisterValidator> tournamentTeamRegisterValidators;

    @Autowired
    private List<TournamentRetryRegisterValidator> tournamentRetryRegisterValidators;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private PaymentEventRepository eventRepository;

    public GeneratedPaymentData registerTeam(Long tournamentId, TournamentRegistrationData registrationData,
                                             MultipartFile teamShieldFile,
                                             Authentication authentication) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

        User captain = (User) authentication.getPrincipal();

        if (registrationData.teamData() == null && registrationData.paymentData() != null) {
            log.info("[REGISTRO] Tentativa de re-inscrição | torneio={} | capitão={}",
                    tournament.getName(), captain.getUsername());
            return retryRegisterTeam(tournament, captain, registrationData.paymentData());
        }

        log.info("[REGISTRO] Iniciando inscrição de equipe | torneio={} | equipe={} | capitão={}",
                tournament.getName(), registrationData.teamData().teamName(), captain.getUsername());

        tournamentTeamRegisterValidators.forEach(v -> v.validate(registrationData, tournament));

        TeamRegisterData teamData = registrationData.teamData();
        PaymentRegistrationDataMercadoPago paymentData = registrationData.paymentData();

        String shieldUrl = null;
        if (teamShieldFile != null) {
            shieldUrl = imgurClient.uploadShield(teamShieldFile, teamData.teamName());
        }

        Team team = new Team(teamData, captain, tournament);
        team.setShieldUrl(shieldUrl);

        GeneratedPaymentData result = linkPaymentInTeam(team, paymentData);

        log.info("[REGISTRO] Equipe inscrita com sucesso | torneio={} | equipe={} | paymentUuid={}",
                tournament.getName(), team.getName(), result.uuid());

        return result;
    }

    private GeneratedPaymentData retryRegisterTeam(Tournament tournament, User captain, PaymentRegistrationDataMercadoPago paymentData) {
        Optional<Team> team = teamRepository.findByCaptainIdAndTournamentIdAndStatusNot(captain.getId(), tournament.getId(), TeamStatus.CANCELED);

        if (team.isEmpty()) {
            throw new RuntimeException("Não existe equipe cadastrada para retry");
        }

        Team teamToRetry = team.get();
        tournamentRetryRegisterValidators.forEach(v -> v.validate(tournament, teamToRetry));

        GeneratedPaymentData result = linkPaymentInTeam(teamToRetry, paymentData);

        log.info("[REGISTRO] Re-inscrição realizada com sucesso | torneio={} | equipe={} | paymentUuid={}",
                tournament.getName(), teamToRetry.getName(), result.uuid());

        return result;
    }

    public GeneratedPaymentData linkPaymentInTeam(Team teamToRegister, PaymentRegistrationDataMercadoPago paymentData) {
        PaymentEntity newPayment = generateNewPayment(teamToRegister, paymentData);
        newPayment.linkTeam(teamToRegister);
        teamToRegister.paymentGenerated(newPayment);

        teamRepository.save(teamToRegister);

        PaymentEntity savedPayment = paymentRepository
                .findByMercadoPagoId(newPayment.getMercadoPagoId())
                .orElseGet(() -> paymentRepository.save(newPayment));

        eventRepository.save(new PaymentEvent(savedPayment, PaymentEventType.PAYMENT_GENERATED));
        return new GeneratedPaymentData(savedPayment);
    }

    public PaymentEntity generateNewPayment(Team team, PaymentRegistrationDataMercadoPago paymentData) {
        String payerName = paymentData.nome() + " " + paymentData.sobrenome();
        BigDecimal value = calculateRegistrationPrice(team.getPlayers().size());
        Payment generatedPayment = mercadoPagoClient.emitPayment(paymentData, value);
        return new PaymentEntity(generatedPayment, payerName);
    }

    public CanceledTeamData cancelTeam(Long tournamentId, CancelRegistrationDto updateDTO, Authentication authentication) {
        validateAuthentication(authentication);

        User captain = (User) authentication.getPrincipal();
        Tournament tournament = getTournamentOrThrow(tournamentId);
        validateTournamentStatus(tournament);

        if (!updateDTO.cancelRegistration()) {
            throw new IllegalStateException("Operação não permitida");
        }

        Optional<Team> optionalTeam = findTeam(captain, tournament.getId());
        if (optionalTeam.isEmpty()) {
            throw new TeamNotFoundException("Não foi encontrada nenhuma equipe cadastrada para cancelar!");
        }

        Team teamToCancel = optionalTeam.get();
        PaymentEntity paymentToCancel = teamToCancel.getPayments().getLast();

        log.info("[REGISTRO] Solicitação de cancelamento de inscrição | torneio={} | equipe={} | capitão={}",
                tournament.getName(), teamToCancel.getName(), captain.getUsername());

        if (!paymentToCancel.isCanceled()) {
            boolean response = mercadoPagoClient.cancelPayment(paymentToCancel.getMercadoPagoId(), paymentToCancel.getId());
            if (!response) {
                log.warn("[REGISTRO] Falha ao cancelar pagamento no MP durante cancelamento de inscrição | equipe={} | mpId={}",
                        teamToCancel.getName(), paymentToCancel.getMercadoPagoId());
            }
            paymentToCancel.cancelByUser();
        }

        teamToCancel.cancelByUser();
        eventRepository.save(new PaymentEvent(paymentToCancel, PaymentEventType.CANCELED_BY_USER));
        teamRepository.save(teamToCancel);

        log.info("[REGISTRO] Inscrição cancelada com sucesso | torneio={} | equipe={}",
                tournament.getName(), teamToCancel.getName());

        return new CanceledTeamData(teamToCancel);
    }

    public boolean checkExistentTeamNameInTournament(Long tournamentId, String name) {
        List<TeamStatus> status = Arrays.asList(
                TeamStatus.EXPIRED_PAYMENT,
                TeamStatus.EXPIRED_PAYMENT_PROBLEM,
                TeamStatus.CANCELED
        );
        return teamRepository.existsByNameIgnoreCaseAndTournamentIdAndStatusNotIn(name, tournamentId, status);
    }

    public TournamentRegistrationStatusData getRegistrationStatus(String tournamentSlug, Authentication authentication) {
        validateAuthentication(authentication);
        User captain = (User) authentication.getPrincipal();
        Tournament tournament = tournamentRepository.findBySlug(tournamentSlug).orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
        validateTournamentStatus(tournament);

        Optional<Team> team = findTeam(captain, tournament.getId());
        boolean maxTeamsReached = isMaxTeamsReached(tournament);

        if (team.isPresent()) {
            return handleExistingTeam(team.get(), tournament, maxTeamsReached);
        }

        return handleNoTeam(tournament, maxTeamsReached);
    }

    // ─── Auxiliares ────────────────────────────────────────────────────────────

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }
    }

    private Tournament getTournamentOrThrow(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));
    }

    private void validateTournamentStatus(Tournament tournament) {
        if (tournament.getStatus() != TournamentStatus.OPEN && tournament.getStatus() != TournamentStatus.FULL) {
            throw new TournamentFullException("As inscrições para esse torneio estão encerradas!");
        }
    }

    private Optional<Team> findTeam(User captain, Long tournamentId) {
        return teamRepository.findByCaptainIdAndTournamentIdAndStatusNot(
                captain.getId(), tournamentId, TeamStatus.CANCELED);
    }

    private boolean isMaxTeamsReached(Tournament tournament) {
        return teamRepository.countByTournamentId(tournament.getId()) >= tournament.getMaxTeams();
    }

    private TournamentRegistrationStatusData handleExistingTeam(Team team, Tournament tournament, boolean maxTeamsReached) {
        if (tournament.getStatus() == TournamentStatus.FULL || maxTeamsReached) {
            CheckRegistrationData registrationData = new CheckRegistrationData(tournament.getId(), true, team.getStatus(), team.getPlayersCount(), tournament.getStatus(), true);
            return new TournamentRegistrationStatusData(registrationData);
        }

        PaymentEntity payment = team.getPayments().getLast();

        if (tournament.getStatus() == TournamentStatus.OPEN && payment.isPending()) {
            GeneratedPaymentData generatedPaymentData = new GeneratedPaymentData(payment);

            CheckRegistrationData registrationData = new CheckRegistrationData(tournament.getId(), true, team.getStatus(), team.getPlayersCount(), tournament.getStatus(), false);
            return new TournamentRegistrationStatusData(registrationData, generatedPaymentData);
        }

        CheckRegistrationData registrationData = new CheckRegistrationData(tournament.getId(), true, team.getStatus(), team.getPlayersCount(), tournament.getStatus(), false);
        return new TournamentRegistrationStatusData(registrationData);
    }

    private TournamentRegistrationStatusData handleNoTeam(Tournament tournament, boolean maxTeamsReached) {
        CheckRegistrationData registrationData = new CheckRegistrationData(tournament.getId(), false, tournament.getStatus(), maxTeamsReached);
        return new TournamentRegistrationStatusData(registrationData);
    }

    private BigDecimal calculateRegistrationPrice(Integer playersAmount) {
        return playersAmount == 5 ? new BigDecimal(5) : new BigDecimal(6);
    }
}
