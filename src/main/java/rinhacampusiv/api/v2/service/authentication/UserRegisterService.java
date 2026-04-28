package rinhacampusiv.api.v2.service.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rinhacampusiv.api.v2.domain.auth.activation.AccountActivationToken;
import rinhacampusiv.api.v2.domain.auth.activation.AccountActivationTokenRepository;
import rinhacampusiv.api.v2.domain.auth.RegisterData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.auth.EmailAlreadyExistsException;
import rinhacampusiv.api.v2.infra.exception.auth.UsernameAlreadyExistsException;
import rinhacampusiv.api.v2.service.email.EmailService;

@Service
public class UserRegisterService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AccountActivationTokenRepository activationTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterData data) {
        if (repository.existsByUsername(data.username())) {
            throw new UsernameAlreadyExistsException();
        }
        if (repository.existsByEmail(data.email())) {
            throw new EmailAlreadyExistsException();
        }

        // Cria o usuário com active = false — fica inativo até ativar pelo email
        String encryptedPassword = passwordEncoder.encode(data.password());
        RegisterData registerData = new RegisterData(data.username(), data.email(), encryptedPassword);
        User newUser = new User(registerData);
        repository.save(newUser);

        // Gera o token de ativação
        AccountActivationToken activationToken = new AccountActivationToken(newUser);
        activationTokenRepository.save(activationToken);

        // Envia o email de confirmação de forma assíncrona
        emailService.sendAccountConfirmationEmail(
                newUser.getEmail(),
                newUser.getUsername(),
                activationToken.getToken()
        );
    }
}