package rinhacampusiv.api.v2.controller.tournaments;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.CancelRegistrationDto;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.CanceledTeamData;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentRegistrationStatusData;
import rinhacampusiv.api.v2.service.tournament.registration.TournamentRegistrationService;

import java.net.URI;


@RestController
@RequestMapping("/tournaments")
public class TournamentRegistrationController {

    @Autowired
    private TournamentRegistrationService tournamentRegistrationService;

    @PutMapping(value = "/{tournamentId}/registrations")
    public ResponseEntity<CanceledTeamData> cancelRegistration(
            @RequestBody @Valid CancelRegistrationDto cancelRegistration,
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        CanceledTeamData updatedTeam = tournamentRegistrationService.updateTeam(tournamentId,cancelRegistration, authentication);
        return ResponseEntity.ok(updatedTeam);
    }


    // Controller
    @PostMapping(value = "/{tournamentId}/registrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedPaymentData> register(
            @PathVariable Long tournamentId,
            @RequestPart(value = "teamData", required = false) @Valid TeamRegisterData teamData,
            @RequestPart("paymentData") @Valid PaymentRegistrationDataMercadoPago paymentData,
            @RequestPart(value = "teamShield", required = false) MultipartFile teamShield,
            Authentication authentication) {

        var registrationData = new TournamentRegistrationData(teamData,paymentData);

        GeneratedPaymentData result = tournamentRegistrationService.registerTeam(
                tournamentId, registrationData, teamShield, authentication
        );

        URI uri = URI.create("/payments/" + result.uuid());

        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping(value = "/{tournamentId}/registrations")
    public ResponseEntity<TournamentRegistrationStatusData> registrationStatus(
            @PathVariable Long tournamentId,
            Authentication authentication) {

        var registrationStatus = tournamentRegistrationService.getRegistrationStatus(
                tournamentId, authentication
        );

        return ResponseEntity.ok(registrationStatus);


    }

    @GetMapping("/{tournamentId}/teams/check-name")
    public ResponseEntity<Void> checkTeamName(
            @PathVariable Long tournamentId,
            @RequestParam String name) {

        boolean existTeam = tournamentRegistrationService.checkExistentTeamNameInTournament(tournamentId, name.trim());

        if(!existTeam){return ResponseEntity.noContent().build();}
        return ResponseEntity.ok().build();
    }



}
