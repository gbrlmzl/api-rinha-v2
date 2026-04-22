package rinhacampusiv.api.v2.controller.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.passwordReset.NewPasswordRequest;
import rinhacampusiv.api.v2.domain.passwordReset.PasswordResetRequest;
import rinhacampusiv.api.v2.service.user.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Solicita reset — envia email
    @PostMapping
    public ResponseEntity<?> requestReset(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request.username());
        return ResponseEntity.ok(Map.of(
                "message", "Se o username existir, um email será enviado em breve"
        ));
    }

    // Valida o token — frontend chama ao carregar /nova-senha
    @GetMapping("/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        boolean valid = passwordResetService.validateToken(token);
        if (!valid) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token inválido ou expirado"));
        }
        return ResponseEntity.ok(Map.of("valid", true));
    }

    // Aplica a nova senha
    @PatchMapping("/{token}")
    public ResponseEntity<?> confirmReset(
            @PathVariable String token,
            @RequestBody @Valid NewPasswordRequest request) {
        passwordResetService.resetPassword(token, request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso"));
    }
}