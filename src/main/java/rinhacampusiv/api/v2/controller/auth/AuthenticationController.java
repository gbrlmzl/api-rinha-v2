package rinhacampusiv.api.v2.controller.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.auth.*;
import rinhacampusiv.api.v2.domain.user.*;
import rinhacampusiv.api.v2.service.authentication.UserAuthService;
import rinhacampusiv.api.v2.service.authentication.UserRegisterService;
import rinhacampusiv.api.v2.service.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserService userService;




    @PostMapping("/register")
    @Transactional
    public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody @Valid RegisterData data) {
        userRegisterService.registerUser(data);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse("Usuário cadastrado com sucesso.\nLink de confirmação da conta enviado via email."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginData data) {
        GeneratedAuthCookies cookies = userAuthService.login(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.accessTokenCookie())
                .header(HttpHeaders.SET_COOKIE, cookies.refreshTokenCookie())
                .body(new LoginResponse("Login efetuado com sucesso.")); // opcionalmente devolve algo curto

    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {

        GeneratedAuthCookies cleanCookies = userAuthService.logout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookies.accessTokenCookie())
                .header(HttpHeaders.SET_COOKIE, cleanCookies.refreshTokenCookie())
                .body(new LogoutResponse("Sessão encerrada."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(HttpServletRequest request) {
        String newAccessCookie = userAuthService.refresh(request);

        return ResponseEntity.ok()
                .header("Set-Cookie", newAccessCookie)
                .body(new RefreshTokenResponse("Token renovado com sucesso."));
    }



    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDataResponse> getAuthenticatedUserData (HttpServletRequest request) {

        UserEssentialsDetails user = userService.getAuthenticatedUser(request);

        return ResponseEntity.ok(
                new AuthenticatedUserDataResponse(
                        user.username(),
                        user.nickname(),
                        user.email(),
                        user.profilePic() != null ? user.profilePic() : "",
                        user.role().name()));
    }

}
