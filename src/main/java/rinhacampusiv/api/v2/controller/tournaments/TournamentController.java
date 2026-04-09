package rinhacampusiv.api.v2.controller.tournaments;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.*;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TeamRepository teamRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> createTournament(
            @RequestBody @Valid TournamentCreationData tournamentDto) {

        // 1. Valida datas
        if (tournamentDto.startsAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("A data de início não pode ser no passado");
        }
        if (tournamentDto.endsAt().isBefore(tournamentDto.startsAt())) {
            throw new RuntimeException("A data de término não pode ser antes da data de início");
        }

        // 2. Valida nome duplicado
        if (tournamentRepository.existsByNameAndGame(tournamentDto.name(), tournamentDto.game())) {
            throw new RuntimeException("Já existe um torneio com esse nome para este jogo");
        }

        // 3. Valida torneio aberto simultâneo
        if (tournamentRepository.existsByGameAndStatus(tournamentDto.game(), TournamentStatus.OPEN)) {
            throw new RuntimeException("Já existe um torneio aberto para este jogo");
        }

        Tournament tournament = new Tournament(tournamentDto);
        tournamentRepository.save(tournament);

        URI uri = URI.create("/tournaments/" + tournament.getId());
        return ResponseEntity.created(uri).body(new TournamentDetailData(tournament));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TournamentDetailData> getTournament(@PathVariable Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneio não encontrado"));

        return ResponseEntity.ok(new TournamentDetailData(tournament));
    }

    @GetMapping("/{tournamentId}/teams/check-name")
    public ResponseEntity<Void> checkTeamName(
            @PathVariable Long tournamentId,
            @RequestParam String name) {

        String cleanedName = name.trim();

        Boolean exists = teamRepository.existsByNameIgnoreCaseAndTournamentId(cleanedName, tournamentId);

        return exists
                ? ResponseEntity.status(HttpStatus.CONFLICT).build()
                : ResponseEntity.noContent().build();
    }
}
