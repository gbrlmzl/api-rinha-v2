package rinhacampusiv.api.v2.controller.tournaments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.MyTournamentsSummaryData;
import rinhacampusiv.api.v2.service.tournaments.MyTournamentsService;

@RestController
@RequestMapping("/tournaments/me")
public class MyTournamentController {

    @Autowired
    private MyTournamentsService myTournamentsService;

    @GetMapping
    public ResponseEntity<Page<MyTournamentsSummaryData>> getMyTournaments(
            @RequestParam TournamentGame game,
            @PageableDefault(size = 12, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(myTournamentsService.getMyActiveTournaments(game, pageable, authentication.getName()));
    }


}
