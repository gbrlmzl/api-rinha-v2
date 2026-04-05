package rinhacampusiv.api.v2.controller.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.passwordReset.NewPasswordRequest;
import rinhacampusiv.api.v2.domain.passwordReset.PasswordResetRequest;
import rinhacampusiv.api.v2.service.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // POST /auth/password-reset/request
    // Recebe o username e envia o email
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestReset(
            @RequestBody @Valid PasswordResetRequest request) {

        passwordResetService.requestPasswordReset(request.username());

        // Sempre retorna 200 — não revela se o username existe ou não
        return ResponseEntity.ok(Map.of(
                "message", "Se o username existir, um email será enviado em breve"
        ));
    }

    // GET /auth/password-reset/validate?token=xxx
    // Frontend chama ao carregar a página /nova-senha para verificar se o token é válido
    @GetMapping("/password-reset/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token inválido ou expirado"));
        }

        return ResponseEntity.ok(Map.of("valid", true));
    }

    // POST /auth/password-reset/confirm
    // Recebe o token e a nova senha
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmReset(
            @RequestBody @Valid NewPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.token(), request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}