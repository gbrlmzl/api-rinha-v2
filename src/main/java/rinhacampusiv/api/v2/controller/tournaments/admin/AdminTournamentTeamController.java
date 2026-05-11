package rinhacampusiv.api.v2.controller.tournaments.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.teams.dtos.TeamAdminSummaryData;
import rinhacampusiv.api.v2.service.tournaments.admin.AdminTournamentTeamService;

import java.util.List;

@RestController
@RequestMapping("/admin/tournaments/{tournamentId}/teams")
public class AdminTournamentTeamController {

    @Autowired
    private AdminTournamentTeamService adminService;

    @GetMapping
    public ResponseEntity<Page<TeamAdminSummaryData>> listTeams(
            @PathVariable Long tournamentId,
            @RequestParam(defaultValue = "PENDING_PAYMENT, READY, FINISHED") List<TeamStatus> statusList,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.listTeams(tournamentId, statusList,pageable));
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<Void> banTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId) {
        adminService.banTeam(tournamentId, teamId);
        return ResponseEntity.noContent().build();
    }
}
