package rinhacampusiv.api.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.activation.AccountActivationToken;
import rinhacampusiv.api.v2.domain.activation.AccountActivationTokenRepository;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.InvalidTokenException;

@Service
public class AccountActivationService {

    @Autowired
    private AccountActivationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(AccountActivationToken::isValid)
                .orElse(false);
    }

    @Transactional
    public void activateAccount(String token) {
        AccountActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        if (!activationToken.isValid()) {
            throw new InvalidTokenException(
                    activationToken.isExpired() ? "Token expirado" : "Token já utilizado"
            );
        }

        activationToken.getUser().activate();
        activationToken.markAsUsed();
    }

    // Reenvio de email — para quando o token expirou
    @Transactional
    public void resendActivationEmail(String username) {
        User user = (User) userRepository.findByUsername(username);

        // Busca silenciosa — não revela se o username existe ou se já está ativo
        if (user == null || user.isActive()) return;

        // Remove tokens anteriores e gera um novo
        tokenRepository.deleteAllByUserId(user.getId());

        AccountActivationToken newToken = new AccountActivationToken(user);
        tokenRepository.save(newToken);

        emailService.sendAccountConfirmationEmail(
                user.getEmail(),
                user.getUsername(),
                newToken.getToken()
        );
    }
}