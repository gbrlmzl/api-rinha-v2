package rinhacampusiv.api.v2.service.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.auth.GeneratedAuthCookies;
import rinhacampusiv.api.v2.domain.auth.LoginData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.auth.RefreshTokenNotFoundException;
import rinhacampusiv.api.v2.infra.exception.users.AccountNotActivatedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Arrays;

@Service
public class UserAuthService {

    private static final String REFRESH_COOKIE_NAME = "REFRESH";

    @Autowired private TokenService tokenService;
    @Autowired private CookieService cookieService;
    @Autowired private AuthenticationManager authenticationManager;

    // ── Casos de uso ──────────────────────────────────────────────────────────

    public GeneratedAuthCookies login(LoginData data) {
        User user = authenticate(data);

        if (!user.isActive()) {
            throw new AccountNotActivatedException();
        }

        String accessToken  = tokenService.generateToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return cookieService.generateCookies(accessToken, refreshToken, data.keepLoggedIn());
    }

    public GeneratedAuthCookies logout() {
        return cookieService.generateCleanCookies();
    }

    public String refresh(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);

        String subject  = tokenService.getSubjectFromRefreshToken(refreshToken);
        String username = tokenService.getUsernameFromRefreshToken(refreshToken);

        String newAccessToken = tokenService.generateToken(subject, username);

        return cookieService.buildAccessCookie(newAccessToken);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private User authenticate(LoginData data) {
        var authToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        return (User) authenticationManager.authenticate(authToken).getPrincipal();
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new RefreshTokenNotFoundException("Refresh token não encontrado");
        }

        return Arrays.stream(cookies)
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token não encontrado"));
    }
}