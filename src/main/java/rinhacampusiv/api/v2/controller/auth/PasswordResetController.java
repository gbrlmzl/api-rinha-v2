package rinhacampusiv.api.v2.controller.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.Validate;
import rinhacampusiv.api.v2.domain.auth.activation.ValidateTokenResponseDTO;
import rinhacampusiv.api.v2.domain.auth.passwordReset.ConfirmResetPasswordResponse;
import rinhacampusiv.api.v2.domain.auth.passwordReset.NewPasswordRequest;
import rinhacampusiv.api.v2.domain.auth.passwordReset.PasswordResetRequest;
import rinhacampusiv.api.v2.domain.auth.passwordReset.RequestResetPasswordResponse;
import rinhacampusiv.api.v2.service.user.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Solicita reset — envia email
    @PostMapping
    public ResponseEntity<RequestResetPasswordResponse> requestReset(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request.username());
        return ResponseEntity.ok(new RequestResetPasswordResponse("Se o username existir, um email será enviado em breve"));
    }

    // Valida o token — frontend chama ao carregar /nova-senha
    @GetMapping("/{token}")
    public ResponseEntity<ValidateTokenResponseDTO> validateToken(@PathVariable String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok(new ValidateTokenResponseDTO(true));
    }

    // Aplica a nova senha
    @PatchMapping("/{token}")
    public ResponseEntity<ConfirmResetPasswordResponse> confirmReset(
            @PathVariable String token,
            @RequestBody @Valid NewPasswordRequest request) {
        passwordResetService.resetPassword(token, request.newPassword());
        return ResponseEntity.ok(new ConfirmResetPasswordResponse("Senha redefinida com sucesso"));
    }
}