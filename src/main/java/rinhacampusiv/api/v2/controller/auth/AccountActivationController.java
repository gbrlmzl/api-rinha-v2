package rinhacampusiv.api.v2.controller.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.auth.activation.ActivateAccountResponseDTO;
import rinhacampusiv.api.v2.domain.auth.activation.ResendActivationEmailResponseDTO;
import rinhacampusiv.api.v2.domain.auth.activation.ResendRequestDTO;
import rinhacampusiv.api.v2.domain.auth.activation.ValidateTokenResponseDTO;
import rinhacampusiv.api.v2.service.user.AccountActivationService;

@RestController
@RequestMapping("/auth")
public class AccountActivationController {

    @Autowired
    private AccountActivationService activationService;

    // GET /auth/activate/validate?token=xxx
    @GetMapping("/activate/validate")
    public ResponseEntity<ValidateTokenResponseDTO> validateToken(@RequestParam String token) {
        activationService.validateToken(token);
        return ResponseEntity.ok(new ValidateTokenResponseDTO(true));
    }

    // POST /auth/activate?token=xxx
    @PostMapping("/activate")
    public ResponseEntity<ActivateAccountResponseDTO> activate(@RequestParam String token) {
        activationService.activateAccount(token);

        return ResponseEntity.ok(new ActivateAccountResponseDTO("Conta ativada com sucesso"));


    }

    // POST /auth/activate/resend — reenvio para token expirado
    @PostMapping("/activate/resend")
    public ResponseEntity<ResendActivationEmailResponseDTO> resend(@RequestBody @Valid ResendRequestDTO request) {
        activationService.resendActivationEmail(request.username());
        // Sempre retorna 200 — não revela se o username existe ou se já está ativo
        return ResponseEntity.ok(new ResendActivationEmailResponseDTO("Se a conta existir e não estiver ativa, um novo email será enviado"));
    }


}