package rinhacampusiv.api.v2.controller.tournaments;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.registrations.GeneratedPaymentData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.TournamentRegistrationData;
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

    @Autowired
    private TournamentRepository tournamentRepository;



    @PostMapping("/{tournamentId}/registrations")
    @Transactional
    public ResponseEntity<GeneratedPaymentData> register(
            @PathVariable Long tournamentId,
            @RequestBody @Valid TournamentRegistrationData registrationData,
            Authentication authentication){

        // Busca o torneio pelo ID da URL — lança 404 se não existir
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));

        User captain = (User) authentication.getPrincipal();

        GeneratedPaymentData result = processTournamentRegistration.registerTeam(
                registrationData,
                tournament,
                captain
        );

        URI uri = URI.create("/payments/" + result.mercadoPagoPaymentId());

        return ResponseEntity.created(uri).body(result);

    }





}
