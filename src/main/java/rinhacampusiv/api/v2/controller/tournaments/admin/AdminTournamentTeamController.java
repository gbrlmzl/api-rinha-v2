package rinhacampusiv.api.v2.controller.tournaments.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamAdminSummaryData;
import rinhacampusiv.api.v2.service.tournament.admin.AdminTournamentTeamService;

@RestController
@RequestMapping("/admin/tournaments/{tournamentId}/teams")
public class AdminTournamentTeamController {

    @Autowired
    private AdminTournamentTeamService adminService;

    @GetMapping
    public ResponseEntity<Page<TeamAdminSummaryData>> listTeams(
            @PathVariable Long tournamentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.listTeams(tournamentId, pageable));
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<Void> banTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId) {
        adminService.banTeam(tournamentId, teamId);
        return ResponseEntity.noContent().build();
    }
}
