package rinhacampusiv.api.v2.controller.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.user.GeneratedAuthCookies;
import rinhacampusiv.api.v2.domain.user.LoginData;
import rinhacampusiv.api.v2.domain.user.RegisterData;
import rinhacampusiv.api.v2.domain.user.UserEssentialsDetails;
import rinhacampusiv.api.v2.service.user.UserService;
import rinhacampusiv.api.v2.service.authentication.UserAuthService;
import rinhacampusiv.api.v2.service.authentication.UserRegisterService;

import java.util.Map;

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
    public ResponseEntity<?> registerAction(@RequestBody @Valid RegisterData data) {

        userRegisterService.registerUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "Usuário cadastrado com sucesso.\nLink de confirmação da conta enviado via email."));

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAction(@RequestBody @Valid LoginData data) {

        GeneratedAuthCookies cookies = userAuthService.login(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.accessTokenCookie())
                .header(HttpHeaders.SET_COOKIE, cookies.refreshTokenCookie())
                .body(Map.of("status", "ok")); // opcionalmente devolve algo curto

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        GeneratedAuthCookies cleanCookies = userAuthService.logout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookies.accessTokenCookie())
                .header(HttpHeaders.SET_COOKIE, cleanCookies.refreshTokenCookie())
                .body(Map.of("status", "ok"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        String newAccessCookie = userAuthService.refresh(request);

        return ResponseEntity.ok()
                .header("Set-Cookie", newAccessCookie)
                .body("Token renovado");
    }



    @GetMapping("/me")
    public ResponseEntity<?> me (HttpServletRequest request) {

        UserEssentialsDetails user = userService.getAuthenticatedUser(request);

        return ResponseEntity.ok(Map.of(
                "username",   user.username(),
                "nickname",   user.nickname(),
                "email",      user.email(),
                "profilePic", user.profilePic() != null ? user.profilePic() : ""
        ));

    }

}
