package rinhacampusiv.api.v2.service.authentication;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.auth.GeneratedAuthCookies;

@Service
public class CookieService {

    // ── Nomes dos cookies ─────────────────────────────────────────────────────
    private static final String ACCESS_COOKIE_NAME  = "JWT";
    private static final String REFRESH_COOKIE_NAME = "REFRESH";

    // ── Durações em segundos ──────────────────────────────────────────────────
    private static final int ACCESS_TOKEN_AGE          = 15 * 60;           // 15 min
    private static final int REFRESH_TOKEN_AGE_DEFAULT = 7  * 24 * 60 * 60; //  7 dias
    private static final int REFRESH_TOKEN_AGE_KEEP    = 30 * 24 * 60 * 60; // 30 dias

    // ─────────────────────────────────────────────────────────────────────────

    public GeneratedAuthCookies generateCookies(
            String accessToken,
            String refreshToken,
            boolean keepLoggedIn) {

        int refreshTokenAge = keepLoggedIn
                ? REFRESH_TOKEN_AGE_KEEP
                : REFRESH_TOKEN_AGE_DEFAULT;

        ResponseCookie accessCookie = buildCookie(ACCESS_COOKIE_NAME, accessToken, ACCESS_TOKEN_AGE);
        ResponseCookie refreshCookie = buildCookie(REFRESH_COOKIE_NAME, refreshToken, refreshTokenAge);

        return new GeneratedAuthCookies(accessCookie.toString(), refreshCookie.toString());
    }

    public GeneratedAuthCookies generateCleanCookies() {
        ResponseCookie clearAccess  = buildCookie(ACCESS_COOKIE_NAME,  "", 0);
        ResponseCookie clearRefresh = buildCookie(REFRESH_COOKIE_NAME, "", 0);

        return new GeneratedAuthCookies(clearAccess.toString(), clearRefresh.toString());
    }

    // ── Builder interno ───────────────────────────────────────────────────────

    /**
     * Constrói um ResponseCookie com as configurações de segurança padrão do projeto.
     * Centraliza httpOnly, secure, path e sameSite para evitar divergências entre cookies.
     */
    private ResponseCookie buildCookie(String name, String value, int maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    public String buildAccessCookie(String accessToken) {
        return buildCookie(ACCESS_COOKIE_NAME, accessToken, ACCESS_TOKEN_AGE).toString();
    }
}