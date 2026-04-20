package rinhacampusiv.api.v2.controller.tournaments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;
import rinhacampusiv.api.v2.service.tournament.PublicTournamentsService;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class PublicTournamentController {

    @Autowired
    private PublicTournamentsService publicService;

    //Listagem Pública
    //Implementar a verificação para a seção de torneios FINISHED(Hall of Fame, No Service)
    @GetMapping
    public ResponseEntity<Page<TournamentPublicSummaryData>> listTournaments(
            @RequestParam TournamentGame game,
            @RequestParam(defaultValue = "OPEN,FULL,ONGOING") List<TournamentStatus> status,
            @PageableDefault(size = 9, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(publicService.listByGameAndStatusIn(game, status, pageable));
    }

    // Detalhe público
    @GetMapping("/{id}")
    public ResponseEntity<TournamentPublicDetailData> getTournament(@PathVariable Long id) {
        return ResponseEntity.ok(publicService.getPublicTournamentView(id));
    }

}
