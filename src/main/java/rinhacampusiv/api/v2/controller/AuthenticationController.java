package rinhacampusiv.api.v2.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rinhacampusiv.api.v2.domain.user.AuthenticationData;
import rinhacampusiv.api.v2.domain.user.RegisterData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.security.SecurityConfigurations;
import rinhacampusiv.api.v2.infra.security.TokenJWTData;
import rinhacampusiv.api.v2.infra.security.TokenService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SecurityConfigurations security;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    @Transactional
    public ResponseEntity registerAction(@RequestBody @Valid RegisterData data) {
        if (repository.existsByUsername(data.username())){
            return ResponseEntity.badRequest().body("Usuário já existe"); //Tratar essas exceções direito depois
        } else if (repository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body("Email já utilizado");
        }

        String criptPassword = passwordEncoder.encode(data.password());

        var registerData = new RegisterData(data.nickname(), data.username(), data.email(), criptPassword, data.profilePic());
        var newUser = new User(registerData);
        repository.save(newUser);

        return ResponseEntity.ok("Usuário cadastrado com sucesso!");

    }

    @PostMapping("/login")
    public ResponseEntity loginAction(@RequestBody @Valid AuthenticationData data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenJWTData(tokenJWT));

    }

}
