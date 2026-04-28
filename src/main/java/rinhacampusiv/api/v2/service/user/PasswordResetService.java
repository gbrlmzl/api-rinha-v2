package rinhacampusiv.api.v2.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.auth.passwordReset.PasswordResetToken;
import rinhacampusiv.api.v2.domain.auth.passwordReset.PasswordResetTokenRepository;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.auth.InvalidTokenException;
import rinhacampusiv.api.v2.service.email.EmailService;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ─── Etapa 1: solicitar recuperação ───────────────────────────────────────
    @Transactional
    public void requestPasswordReset(String username) {
        // Busca silenciosa — não revela se o username existe (segurança)
        User user = (User) userRepository.findByUsername(username);
        if (user == null) return; // retorna sem erro — não revela que o usuário não existe

        // Remove tokens anteriores do usuário antes de gerar um novo
        tokenRepository.deleteAllByUserId(user.getId());

        // Gera novo token
        PasswordResetToken resetToken = new PasswordResetToken(user);
        tokenRepository.save(resetToken);

        // Envia email de forma assíncrona
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                resetToken.getToken()
        );

    }

    // ─── Etapa 2: validar token (para o frontend verificar antes de mostrar o form) ──
    public void validateToken(String token) {
        boolean valid =  tokenRepository.findByToken(token).map(PasswordResetToken::isValid).orElse(false);
        if(!valid){
            throw new InvalidTokenException("Token inválido ou expirado");
        }
    }

    // ─── Etapa 3: redefinir senha ──────────────────────────────────────────────
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        if (!resetToken.isValid()) {
            throw new InvalidTokenException(
                    resetToken.isExpired() ? "Token expirado" : "Token já utilizado"
            );
        }

        // Atualiza a senha do usuário
        User user = resetToken.getUser();
        user.resetPassword(passwordEncoder.encode(newPassword));

        // Invalida o token — não pode ser reutilizado
        resetToken.markAsUsed();
    }
}