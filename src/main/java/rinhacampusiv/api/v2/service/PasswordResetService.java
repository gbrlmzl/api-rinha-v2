package rinhacampusiv.api.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.passwordReset.PasswordResetToken;
import rinhacampusiv.api.v2.domain.passwordReset.PasswordResetTokenRepository;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;

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
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    // ─── Etapa 3: redefinir senha ──────────────────────────────────────────────
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException(
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