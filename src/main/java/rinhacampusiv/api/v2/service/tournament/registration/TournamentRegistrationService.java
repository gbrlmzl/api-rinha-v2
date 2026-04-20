package rinhacampusiv.api.v2.service.tournament.registration;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentRegistrationStatusData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.TournamentNotExistsException;
import rinhacampusiv.api.v2.infra.exception.UserNotAuthenticatedException;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;
import rinhacampusiv.api.v2.service.tournament.payment.PaymentCreationService;
import rinhacampusiv.api.v2.infra.external.ImgurClient;
import rinhacampusiv.api.v2.validators.tournament.team.register.TournamentTeamRegisterValidator;
import rinhacampusiv.api.v2.validators.tournament.team.register.retry.TournamentRetryRegisterValidator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static rinhacampusiv.api.v2.utils.QrCodeUtil.generateBase64;

@Service
public class TournamentRegistrationService {

    @Autowired
    private PaymentCreationService paymentCreationService;

    @Autowired
    private ImgurClient imgurClient;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private List<TournamentTeamRegisterValidator> tournamentTeamRegisterValidators;

    @Autowired
    private List<TournamentRetryRegisterValidator> tournamentRetryRegisterValidators;

    @Autowired
    private TournamentRepository tournamentRepository;

    //Implementar modulo de fazer o upload para o Imgur e excluir caso o pagamento seja expirado.


    public GeneratedPaymentData registerTeam(Long tournamentId, TournamentRegistrationData registrationData,
                                             MultipartFile teamShieldFile,
                                             Authentication authentication) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotExistsException("Torneio não encontrado"));

        User captain = (User) authentication.getPrincipal();

        if (registrationData.teamData() == null && registrationData.paymentData() != null) {
            return retryRegisterTeam(tournament, captain, registrationData.paymentData(), authentication);
        }

        tournamentTeamRegisterValidators.forEach(v -> v.validate(registrationData, tournament));

        TeamRegisterData teamData = registrationData.teamData();
        PaymentRegistrationDataMercadoPago paymentData = registrationData.paymentData();

        String shieldUrl = null;
        if (teamShieldFile != null) {
            shieldUrl = imgurClient.uploadShield(teamShieldFile, teamData.teamName());
        }

        Team team = new Team(teamData, captain, tournament);
        team.setShieldUrl(shieldUrl);

        return linkPaymentInTeam(team, paymentData);
    }

    private GeneratedPaymentData retryRegisterTeam(Tournament tournament, User captain, PaymentRegistrationDataMercadoPago paymentData, Authentication authentication) {

        //Encontrar equipe vinculada ao captain
        Optional<Team> team = teamRepository.findByCaptainIdAndTournamentIdAndStatusNot(captain.getId(), tournament.getId(), TeamStatus.CANCELED);
        if (team.isEmpty()) {
            throw new RuntimeException("Não existe equipe cadastrada para retry");
        }
        //Como validar esse retry?

        Team teamToRetry = team.get();
        tournamentRetryRegisterValidators.forEach(v -> v.validate(tournament, teamToRetry));

        return linkPaymentInTeam(teamToRetry, paymentData);

    }

    public GeneratedPaymentData linkPaymentInTeam(Team teamToRegister, PaymentRegistrationDataMercadoPago paymentData) {

        PaymentEntity newPayment = generateNewPayment(teamToRegister, paymentData);
        newPayment.linkTeam(teamToRegister);
        teamToRegister.paymentGenerated(newPayment);

        teamRepository.save(teamToRegister);
        return new GeneratedPaymentData(newPayment);
    }

    public PaymentEntity generateNewPayment(Team team, PaymentRegistrationDataMercadoPago paymentData) {

        String payerName = paymentData.nome() + " " + paymentData.sobrenome();


        BigDecimal value = calculateRegistatrionPrice(team.getPlayers().size());


        Payment generatedPayment = paymentCreationService.emitPayment(paymentData, value);

        return new PaymentEntity(generatedPayment, payerName);


    }

    public TournamentRegistrationStatusData getRegistrationStatus(Long tournamentId, Authentication authentication) {
        if (authentication == null) {
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }
        User captain = (User) authentication.getPrincipal();

        if (captain == null) { //Disparar exceção genérica
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }

        Optional<Team> team = teamRepository.findByCaptainIdAndTournamentId(captain.getId(), tournamentId);

        if (team.isPresent()) {
            PaymentEntity payment = team.get().getPayments().getLast();

            PaymentStatus paymentStatus = payment.getStatus();

            if (payment.isPending()) {
                String qrCode = payment.getQrCode();
                String qrCodeBase64 = generateBase64(qrCode);

                return new TournamentRegistrationStatusData(true, paymentStatus, payment.getUuid(), payment.getValue(), qrCode, qrCodeBase64, payment.getExpiresAt());

            } else {
                return new TournamentRegistrationStatusData(true, paymentStatus);
            }

        } else {
            return new TournamentRegistrationStatusData(false);
        }
    }

    public void checkExistentTeamNameInTournament(Long tournamentId, String name) {
        List<TeamStatus> status = Arrays.asList(
                TeamStatus.EXPIRED_PAYMENT,
                TeamStatus.EXPIRED_PAYMENT_PROBLEM,
                TeamStatus.CANCELED
        );

        Boolean foundTeam = teamRepository.existsByNameAndTournamentIdAndStatusNotIn(name, tournamentId, status);

        if (foundTeam) {
            throw new ValidatorException("Já existe uma equipe com esse nome");
        }
    }


    private BigDecimal calculateRegistatrionPrice(Integer playersAmount) {
        BigDecimal value;
        if (playersAmount == 5) {
            value = new BigDecimal(5);
        } else {
            value = new BigDecimal(6);
        }

        return value;

    }


}
