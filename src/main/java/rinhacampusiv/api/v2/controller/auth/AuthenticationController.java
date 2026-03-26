package rinhacampusiv.api.v2.controller.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.user.AuthenticationData;
import rinhacampusiv.api.v2.domain.user.RegisterData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.EmailAlreadyExistsException;
import rinhacampusiv.api.v2.infra.exception.UsernameAlreadyExistsException;
import rinhacampusiv.api.v2.infra.security.SecurityConfigurations;
import rinhacampusiv.api.v2.infra.security.TokenJWTData;
import rinhacampusiv.api.v2.infra.security.TokenService;

import java.util.*;

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
            throw new UsernameAlreadyExistsException();
        } else if (repository.existsByEmail(data.email())) {
            throw new EmailAlreadyExistsException();
        }

        String criptPassword = passwordEncoder.encode(data.password());

        var registerData = new RegisterData(data.username(), data.email(), criptPassword);
        var newUser = new User(registerData);

        repository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "Usuário cadastrado com sucesso"));

    }

    @PostMapping("/login")
    public ResponseEntity loginAction(@RequestBody @Valid AuthenticationData data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = manager.authenticate(authenticationToken);

        var accessToken = tokenService.generateToken((User) authentication.getPrincipal());
        var refreshToken = tokenService.generateRefreshToken((User) authentication.getPrincipal());

        // 3) criar cookie HttpOnly
        ResponseCookie accessCookie = ResponseCookie.from("JWT", accessToken)
                .httpOnly(true)
                .secure(true)             // HTTPS obrigatório em produção
                .path("/")                // scopo do cookie
                .maxAge(2 * 60 * 60)      // 2 horas em segundos
                .sameSite("Lax")         // ajuda a mitigar CSRF (ajuste conforme seu domínio) -> Funciona com API e Front no mesmo dominio
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("status", "ok")); // opcionalmente devolve algo curto

    }

    /*@PostMapping("/login")
    public ResponseEntity<?> loginAction(@RequestBody @Valid AuthenticationData data) {

        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var authentication = manager.authenticate(authenticationToken);

            var accessToken = tokenService.generateToken((User) authentication.getPrincipal());
            var refreshToken = tokenService.generateRefreshToken((User) authentication.getPrincipal());

            ResponseCookie accessCookie = ResponseCookie.from("JWT", accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(2 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/auth/refresh")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Map.of("status", "ok"));

        } catch (BadCredentialsException e) {
            // Usuário não existe ou senha errada — propositalmente genérico (não revela qual dos dois)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));


        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno, tente novamente"));
        }
    }*/

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        // Sobrescreve os cookies com maxAge=0, forçando o navegador a deletá-los
        ResponseCookie clearAccess = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)           // instrui o navegador a apagar imediatamente
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("REFRESH", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")  // deve ser o mesmo path do cookie original
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .body(Map.of("status", "ok"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "REFRESH".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

        String subject = tokenService.getSubjectFromRefreshToken(refreshToken);
        String username = tokenService.getClaimUsernameFromRefreshToken(refreshToken);



        String newAccessToken = tokenService.generateToken(subject, username); //o metodo generateToken espera um User usuario como parametro

        ResponseCookie newAccessCookie = ResponseCookie.from("JWT", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7200)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", newAccessCookie.toString())
                .body("Token renovado");
    }




    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request){
        try{
            //1 Cookie ausente ou inválido
            if (request.getCookies() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não encontrado");
            }


            String accessToken = Arrays.stream(request.getCookies())
                    .filter(c -> "JWT".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));


            String subject = tokenService.getSubject(accessToken);
            Optional<User> userOpt = repository.findById(Long.parseLong(subject));

            if(userOpt.isPresent()){
                var user = userOpt.get();
                return ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "email", user.getEmail(),
                        "profilePic", user.getProfilePic() != null ? user.getProfilePic() : ""

                ));
            } else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }

        } catch (Exception e){
            //2 Token inválido ou expirado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
        }


    }



}
