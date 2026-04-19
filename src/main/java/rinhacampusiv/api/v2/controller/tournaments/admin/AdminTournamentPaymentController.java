package rinhacampusiv.api.v2.controller.tournaments.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEventData;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentStatus;
import rinhacampusiv.api.v2.domain.tournaments.payments.events.PaymentEvent;
import rinhacampusiv.api.v2.service.tournament.admin.AdminTournamentPaymentService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminTournamentPaymentController {

    @Autowired
    private AdminTournamentPaymentService paymentService;

    @GetMapping("/tournaments/{tournamentId}/payments")
    public ResponseEntity<Page<PaymentEventData>> listPayments(
            @PathVariable Long tournamentId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(paymentService.listPayments(tournamentId, status, pageable));
    }

    @GetMapping("/payments/{paymentId}/events")
    public ResponseEntity<List<PaymentEvent>> getEvents(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getEvents(paymentId));
    }
}