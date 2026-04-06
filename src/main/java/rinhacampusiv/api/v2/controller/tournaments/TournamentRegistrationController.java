package rinhacampusiv.api.v2.controller.tournaments;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.registrations.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamShieldData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.service.ProcessTournamentRegistrationService;

import java.net.URI;


@RestController
@RequestMapping("/tournaments")
public class TournamentRegistrationController {

    @Autowired
    private ProcessTournamentRegistrationService processTournamentRegistration;


    // Controller
    @PostMapping(value = "/{tournamentId}/registrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedPaymentData> register(
            @PathVariable Long tournamentId,
            @RequestPart("teamData")    @Valid TeamRegisterData teamData,
            @RequestPart("paymentData") @Valid PaymentRegistrationDataMercadoPago paymentData,
            @RequestPart(value = "teamShield", required = false) MultipartFile teamShield,
            Authentication authentication) {

        // monta o TournamentRegistrationData internamente
        var registrationData = new TournamentRegistrationData(teamData,paymentData);

        GeneratedPaymentData result = processTournamentRegistration.registerTeam(
                tournamentId, registrationData,teamShield, authentication
        );

        URI uri = URI.create("/payments/" + result.uuid());
        return ResponseEntity.created(uri).body(result);
    }





}
