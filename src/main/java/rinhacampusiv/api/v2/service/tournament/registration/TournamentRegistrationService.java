package rinhacampusiv.api.v2.service.tournament.registration;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.CancelRegistrationDto;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.CanceledTeamData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.CheckRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentRegistrationStatusData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.*;
import rinhacampusiv.api.v2.infra.external.mercadopago.MercadoPagoClient;
import rinhacampusiv.api.v2.service.tournament.payment.PaymentAPIManagerService;
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
    private PaymentAPIManagerService paymentAPIManagerService;

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

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    //Implementar modulo de fazer o upload para o Imgur e excluir caso o pagamento seja expirado.


    public GeneratedPaymentData registerTeam(Long tournamentId, TournamentRegistrationData registrationData,
                                             MultipartFile teamShieldFile,
                                             Authentication authentication) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Torneio não encontrado"));

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


        Payment generatedPayment = paymentAPIManagerService.emitPayment(paymentData, value);

        return new PaymentEntity(generatedPayment, payerName);


    }

    /*public TournamentRegistrationStatusData getRegistrationStatus(Long tournamentId, Authentication authentication) {
        if (authentication == null) { throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");}
        User captain = (User) authentication.getPrincipal();
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);

        if (tournament.isEmpty()) { throw new TournamentNotFoundException("Torneio não encontrado");
        } else if (tournament.get().getStatus() != TournamentStatus.OPEN && tournament.get().getStatus() != TournamentStatus.FULL) {
            throw new TournamentFullException("As inscrições para esse torneio estão encerradas!");
        }

        if (captain == null) { //Disparar exceção genérica
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }

        Optional<Team> team = teamRepository.findByCaptainIdAndTournamentIdAndStatusNot(captain.getId(), tournamentId, TeamStatus.CANCELED);
        boolean maxTeamsReached = (teamRepository.countByTournamentId(tournament.get().getId()) >= tournament.get().getMaxTeams());

        if (team.isPresent()) {
            if(tournament.get().getStatus() == TournamentStatus.FULL || maxTeamsReached ){
                CheckRegistrationData registrationData = new CheckRegistrationData(true, team.get().getStatus(),tournament.get().getStatus(),true);
                return new TournamentRegistrationStatusData(registrationData);
            }

            PaymentEntity payment = team.get().getPayments().getLast();
            if (tournament.get().getStatus() == TournamentStatus.OPEN && payment.isPending()) {

                GeneratedPaymentData generatedPaymentData = new GeneratedPaymentData(payment);
                CheckRegistrationData registrationData = new CheckRegistrationData(true, team.get().getStatus(),tournament.get().getStatus(),false);

                return new TournamentRegistrationStatusData(registrationData, generatedPaymentData);

            } else /* Inscrição realizada, pagamento expirado|cancelado e torneio com vagas  {
                CheckRegistrationData registrationData = new CheckRegistrationData(true, team.get().getStatus(),tournament.get().getStatus(),false);
                return new TournamentRegistrationStatusData(registrationData);
            }

        } else {
            CheckRegistrationData registrationData = new CheckRegistrationData(false, tournament.get().getStatus(), maxTeamsReached);
            return new TournamentRegistrationStatusData(registrationData);
        }
    }*/


    public CanceledTeamData updateTeam(Long tournamentId, CancelRegistrationDto updateDTO, Authentication authentication){
        if (authentication == null) {
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }

        User captain = (User) authentication.getPrincipal();

        if (captain == null) {
            throw new UserNotAuthenticatedException("Usuário deve estar autenticado para acessar o recurso");
        }
        if(!updateDTO.cancelRegistration()){
            throw new IllegalStateException("Operação não permitida");
        }

        Optional<Team> optionalTeam = teamRepository.findByCaptainIdAndTournamentId(captain.getId(), tournamentId);
        if(optionalTeam.isEmpty()){
            throw new TeamNotFoundException("Não foi encontrada nenhuma equipe cadastrada para cancelar!");
        }

        Team teamToCancel = optionalTeam.get();
        PaymentEntity paymentToCancel = teamToCancel.getPayments().getLast();

        if(!paymentToCancel.isCanceled()){
            //Se o pagamento ainda não estiver com o status de cancelado, quer dizer que o pagamento do mercadoPago ainda está funcionando.
            //Nesse cenário, deve-se cancelar o pagamento do mercadoPago manualmente.
            boolean response = mercadoPagoClient.cancelPayment(paymentToCancel.getMercadoPagoId());

            if(!response){
                //Log de Falha ao atualizar
                throw new MercadoPagoPaymentException("Erro ao cancelar a inscrição. Tente novamente mais tarde");
            }
            paymentToCancel.cancelByUser();

        }

        teamToCancel.cancelByUser();
        teamRepository.save(teamToCancel);

        return new CanceledTeamData(teamToCancel);
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

    public TournamentRegistrationStatusData getRegistrationStatus(Long tournamentId, Authentication authentication) {
        validateAuthentication(authentication);

        User captain = (User) authentication.getPrincipal();
        Tournament tournament = getTournamentOrThrow(tournamentId);

        validateTournamentStatus(tournament);

        Optional<Team> team = findTeam(captain, tournamentId);
        boolean maxTeamsReached = isMaxTeamsReached(tournament);

        if (team.isPresent()) {
            return handleExistingTeam(team.get(), tournament, maxTeamsReached);
        }

        return handleNoTeam(tournament, maxTeamsReached);
    }

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
            CheckRegistrationData registrationData = new CheckRegistrationData(true, team.getStatus(), tournament.getStatus(), true);
            return new TournamentRegistrationStatusData(registrationData);
        }

        PaymentEntity payment = team.getPayments().getLast();
        if (tournament.getStatus() == TournamentStatus.OPEN && payment.isPending()) {
            GeneratedPaymentData generatedPaymentData = new GeneratedPaymentData(payment);
            CheckRegistrationData registrationData = new CheckRegistrationData(true, team.getStatus(), tournament.getStatus(), false);
            return new TournamentRegistrationStatusData(registrationData, generatedPaymentData);
        }

        CheckRegistrationData registrationData = new CheckRegistrationData(true, team.getStatus(), tournament.getStatus(), false);
        return new TournamentRegistrationStatusData(registrationData);
    }

    private TournamentRegistrationStatusData handleNoTeam(Tournament tournament, boolean maxTeamsReached) {
        CheckRegistrationData registrationData = new CheckRegistrationData(false, tournament.getStatus(), maxTeamsReached);
        return new TournamentRegistrationStatusData(registrationData);
    }






    //Auxiliares
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
