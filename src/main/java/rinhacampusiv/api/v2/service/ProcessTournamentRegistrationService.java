package rinhacampusiv.api.v2.service;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.registrations.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.validators.Validator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessTournamentRegistrationService {

    @Autowired
    private EmitPaymentAPIService emitPaymentService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private List<Validator> validators;

    public GeneratedPaymentData registerTeam(TournamentRegistrationData registrationData, Tournament tournament, User captain){
        validators.forEach(v -> v.validate(registrationData, tournament));

        TeamRegisterData teamData = registrationData.teamData();
        PaymentRegistrationDataMercadoPago paymentData = registrationData.paymentData();

        Team team = null;
        if(teamRepository.existsByNameAndTournamentId(teamData.teamName(), tournament.getId())){
            team = teamRepository.findByNameAndTournamentId(teamData.teamName(), tournament.getId()).get();
        } else {
            team = new Team(teamData, captain, tournament);
        }

        return linkPaymentInTeam(team, paymentData);
    }

    public GeneratedPaymentData linkPaymentInTeam(Team teamToRegister, PaymentRegistrationDataMercadoPago paymentData){
        Optional<Team> teamOptional = teamRepository.findByIdWithPayments(
                teamToRegister.getId());

        if(teamOptional.isPresent()){
            Team teamEntity = teamOptional.get();

            PaymentEntity lastPayment = teamEntity.getPayments().getLast();
            if(lastPayment.getStatus().equals("pending")){

                if(lastPayment.getExpiresAt().isAfter(OffsetDateTime.now().plusMinutes(10))){
                    return new GeneratedPaymentData(lastPayment);
                }
            }
            if(lastPayment.getStatus().equals("PAGAMENTO REALIZADO")){
                return new GeneratedPaymentData(lastPayment);
            }
        }

        PaymentEntity newPayment = generateNewPayment(teamToRegister, paymentData);
        newPayment.linkTeam(teamToRegister);
        teamToRegister.paymentGenerated(newPayment);

        teamRepository.save(teamToRegister);
        return new GeneratedPaymentData(newPayment);
    }

    public PaymentEntity generateNewPayment(Team team, PaymentRegistrationDataMercadoPago paymentData){
        BigDecimal value = null;
        String payerName = paymentData.nome() + " " + paymentData.sobrenome();

        if(team.getPlayers().size() == 5){
            value = new BigDecimal(1);
        } else{
            value = new BigDecimal(2);
        }


        Payment generatedPayment = emitPaymentService.emitPayment(paymentData, value);

        return new PaymentEntity(generatedPayment, payerName);


    }


}
