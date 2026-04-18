package rinhacampusiv.api.v2.service;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.registrations.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRegistrationStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.TournamentNotExistsException;
import rinhacampusiv.api.v2.infra.exception.UserNotAuthenticatedException;
import rinhacampusiv.api.v2.validators.tournamentTeamRegister.TournamentTeamRegisterValidator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static rinhacampusiv.api.v2.utils.QrCodeUtil.generateBase64;

@Service
public class TournamentRegistrationService {

    @Autowired
    private EmitPaymentAPIService emitPaymentService;

    @Autowired
    private ImgurAPIService imgurService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private List<TournamentTeamRegisterValidator> tournamentTeamRegisterValidators;

    @Autowired
    private TournamentRepository tournamentRepository;

    //Implementar modulo de fazer o upload para o Imgur e excluir caso o pagamento seja expirado.

    public GeneratedPaymentData registerTeam(Long tournamentId, TournamentRegistrationData registrationData,
                                             MultipartFile teamShieldFile,
                                             Authentication authentication) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotExistsException("Torneio não encontrado"));

        User captain = (User) authentication.getPrincipal();

        //verifique se existe uma equipe vinculada a esse captain, true -> team == team encontrado : disparar exceção

        tournamentTeamRegisterValidators.forEach(v -> v.validate(registrationData, tournament));


        TeamRegisterData teamData = registrationData.teamData();
        PaymentRegistrationDataMercadoPago paymentData = registrationData.paymentData();


        Team team = null;
        String shieldUrl = null;

        if (teamShieldFile != null) {
            //Upload do escudo via API do IMGUR
            shieldUrl = imgurService.uploadShield(teamShieldFile, teamData.teamName());

        }


        team = new Team(teamData, captain, tournament);
        team.setShieldUrl(shieldUrl);


        return linkPaymentInTeam(team, paymentData);
    }

    public GeneratedPaymentData linkPaymentInTeam(Team teamToRegister, PaymentRegistrationDataMercadoPago paymentData) {
        Optional<Team> teamOptional = teamRepository.findByIdWithPayments(
                teamToRegister.getId());

        /* Verificação não é mais necessária pois os validadores já verificam.

        if (teamOptional.isPresent()) {
            Team teamEntity = teamOptional.get();

            PaymentEntity lastPayment = teamEntity.getPayments().getLast();
            if (lastPayment.getStatus().equals("pending")) {

                if (lastPayment.getExpiresAt().isAfter(OffsetDateTime.now().plusMinutes(10))) {
                    return new GeneratedPaymentData(lastPayment);
                }
            }
            if (lastPayment.getStatus().equals("PAGAMENTO REALIZADO")) {
                return new GeneratedPaymentData(lastPayment);
            }
        }

        */

        PaymentEntity newPayment = generateNewPayment(teamToRegister, paymentData);
        newPayment.linkTeam(teamToRegister);
        teamToRegister.paymentGenerated(newPayment);

        //TODO ->
        //Criar metodo para depois de 30 minutos verificar se o pagamento foi aprovado.
        //Se foi, deixar como está. Se não foi, atualizar o status do PaymentEntity para Expired e o Status da equipe também

        teamRepository.save(teamToRegister);
        return new GeneratedPaymentData(newPayment);
    }

    public PaymentEntity generateNewPayment(Team team, PaymentRegistrationDataMercadoPago paymentData) {

        String payerName = paymentData.nome() + " " + paymentData.sobrenome();


        BigDecimal value = calculateRegistatrionPrice(team.getPlayers().size());


        Payment generatedPayment = emitPaymentService.emitPayment(paymentData, value);

        return new PaymentEntity(generatedPayment, payerName);


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

    public TournamentRegistrationStatus getRegistrationStatus(Long tournamentId, Authentication authentication) {
        if(authentication == null){
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }
        User captain = (User) authentication.getPrincipal();

        if (captain == null) { //Disparar exceção genérica
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }

        Optional<Team> team = teamRepository.findByCaptainIdAndTournamentId(captain.getId(), tournamentId);

        if (team.isPresent()) {
            PaymentEntity payment = team.get().getPayments().getLast();

            String paymentStatus = payment.getStatus();

            if (paymentStatus.equals("pending")) {
                String qrCode = payment.getQrCode();
                String qrCodeBase64 = generateBase64(qrCode);

                return new TournamentRegistrationStatus(true, paymentStatus, payment.getUuid(), payment.getValue(), qrCode, qrCodeBase64, payment.getExpiresAt());

            } else {
                return new TournamentRegistrationStatus(true, paymentStatus);
            }

        } else {
            return new TournamentRegistrationStatus(false);
        }
    }


}
