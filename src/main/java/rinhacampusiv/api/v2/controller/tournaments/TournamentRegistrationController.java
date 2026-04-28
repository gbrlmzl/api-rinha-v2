package rinhacampusiv.api.v2.controller.tournaments;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.NameAvailabilityResponseDTO;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentRegistrationStatusData;
import rinhacampusiv.api.v2.service.tournaments.registration.TournamentRegistrationService;

import java.net.URI;


@RestController
@RequestMapping("/tournaments")
public class TournamentRegistrationController {

    @Autowired
    private TournamentRegistrationService tournamentRegistrationService;

    @PutMapping(value = "/{tournamentId}/registrations")
    public ResponseEntity<CanceledTeamData> cancelRegistrationInTournament(
            @RequestBody @Valid CancelRegistrationDto cancelRegistration,
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        CanceledTeamData updatedTeam = tournamentRegistrationService.cancelTeam(tournamentId, cancelRegistration, authentication);
        return ResponseEntity.ok(updatedTeam);
    }


    // Controller
    @PostMapping(value = "/{tournamentId}/registrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedPaymentData> registerTeamInTournament(
            @PathVariable Long tournamentId,
            @RequestPart(value = "teamData", required = false) @Valid TeamRegisterData teamData,
            @RequestPart("paymentData") @Valid PaymentRegistrationDataMercadoPago paymentData,
            @RequestPart(value = "teamShield", required = false) MultipartFile teamShield,
            Authentication authentication) {

        var registrationData = new TournamentRegistrationData(teamData,paymentData);

        GeneratedPaymentData generatedPaymentData = tournamentRegistrationService.registerTeam(
                tournamentId, registrationData, teamShield, authentication
        );

        URI uri = URI.create("/payments/" + generatedPaymentData.uuid());

        return ResponseEntity.created(uri).body(generatedPaymentData);
    }

    @GetMapping(value = "/{tournamentSlug}/registrations")
    public ResponseEntity<TournamentRegistrationStatusData> getTeamRegistrationStatusInTournament(
            @PathVariable String tournamentSlug,
            Authentication authentication) {

        var registrationStatus = tournamentRegistrationService.getRegistrationStatus(
                tournamentSlug, authentication
        );

        return ResponseEntity.ok(registrationStatus);


    }

    @GetMapping("/{tournamentId}/teams/name-availability")
    public ResponseEntity<NameAvailabilityResponseDTO> checkTeamNameAvailability(
            @PathVariable Long tournamentId,
            @RequestParam String name) {

        var existTeamNameInTournament = tournamentRegistrationService.checkExistentTeamNameInTournament(tournamentId, name.trim());
        if (existTeamNameInTournament) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new NameAvailabilityResponseDTO(false));
        }

        return ResponseEntity.ok(new NameAvailabilityResponseDTO(true));
    }



}
