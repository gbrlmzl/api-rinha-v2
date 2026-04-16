package rinhacampusiv.api.v2.controller.tournaments;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.service.tournament.AdminTournamentsService;

@RestController
@RequestMapping("/admin/tournaments")
public class AdminTournamentController {

    @Autowired
    private AdminTournamentsService adminService;

    @PostMapping
    public ResponseEntity<TournamentAdminDetailData> create(
            @RequestBody TournamentCreationData tournamentData,
            UriComponentsBuilder uriBuilder ) {
        TournamentAdminDetailData response = adminService.createTournament(tournamentData);
        var uri = uriBuilder.path("/admin/tournaments/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TournamentAdminSummaryData>> listTournaments(
            @RequestParam(required = false) TournamentGame game,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TournamentAdminSummaryData> response = adminService.getAllTournaments(game, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentAdminDetailData> getTournamentDetails(@PathVariable Long id) {
        TournamentAdminDetailData response = adminService.getTournamentById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TournamentAdminDetailData> updateTournament(
            @PathVariable Long id,
            @RequestBody @Valid TournamentUpdateData data) {

        TournamentAdminDetailData response = adminService.updateTournament(id, data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTournament(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean force) {

        adminService.deleteTournament(id, force);
        return ResponseEntity.noContent().build();
    }
}
