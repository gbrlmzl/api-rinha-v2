package rinhacampusiv.api.v2.controller.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.service.AccountActivationService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AccountActivationController {

    @Autowired
    private AccountActivationService activationService;

    // GET /auth/activate/validate?token=xxx
    @GetMapping("/activate/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean valid = activationService.validateToken(token);

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token inválido ou expirado"));
        }

        return ResponseEntity.ok(Map.of("valid", true));
    }

    // POST /auth/activate?token=xxx
    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String token) {
        try {
            activationService.activateAccount(token);
            return ResponseEntity.ok(Map.of("message", "Conta ativada com sucesso"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // POST /auth/activate/resend — reenvio para token expirado
    @PostMapping("/activate/resend")
    public ResponseEntity<?> resend(@RequestBody @Valid ResendRequest request) {
        activationService.resendActivationEmail(request.username());

        // Sempre retorna 200 — não revela se o username existe ou se já está ativo
        return ResponseEntity.ok(Map.of(
                "message", "Se a conta existir e não estiver ativa, um novo email será enviado"
        ));
    }

    private record ResendRequest(@NotBlank String username) {}
}