package rinhacampusiv.api.v2.controller.tournaments;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.registrations.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRegistrationStatus;
import rinhacampusiv.api.v2.service.TournamentRegistrationService;
import java.net.URI;


@RestController
@RequestMapping("/tournaments")
public class TournamentRegistrationController {

    @Autowired
    private TournamentRegistrationService tournamentRegistrationService;


    // Controller
    @PostMapping(value = "/{tournamentId}/registrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedPaymentData> registerTeam(
            @PathVariable Long tournamentId,
            @RequestPart(value = "teamData", required = false)    @Valid TeamRegisterData teamData, //Opcional
            @RequestPart("paymentData") @Valid PaymentRegistrationDataMercadoPago paymentData,
            @RequestPart(value = "teamShield", required = false) MultipartFile teamShield, //Opcional
            Authentication authentication) {

        // monta o TournamentRegistrationData internamente

        if(teamData == null){
            //Fluxo de recadastro de equipe existente
            //var registrationData = new TournamentRetryRegistrationData(paymentData);
            System.out.println("Caiu aqui");

            GeneratedPaymentData retryResult = tournamentRegistrationService.retryRegisterTeam(tournamentId, paymentData, authentication);

            URI uri = URI.create("/payments/" + retryResult.uuid());

            return ResponseEntity.created(uri).body(retryResult);

        }

        System.out.println(teamData.teamName());
        var registrationData = new TournamentRegistrationData(teamData,paymentData);

        GeneratedPaymentData result = tournamentRegistrationService.registerTeam(
                tournamentId, registrationData,teamShield, authentication
        );

        URI uri = URI.create("/payments/" + result.uuid());

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping(value = "/{tournamentId}/registrations")
    public ResponseEntity<TournamentRegistrationStatus> registrationStatus(
            @PathVariable Long tournamentId,
            Authentication authentication) {

        var registrationStatus = tournamentRegistrationService.getRegistrationStatus(
                tournamentId, authentication
        );

        return ResponseEntity.ok(registrationStatus);


    }





}
