package rinhacampusiv.api.v2.service.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // ── Constantes ────────────────────────────────────────────────────────────

    private static final String ISSUER        = "API Rinha.v2";
    private static final String CLAIM_TYPE     = "type";
    private static final String CLAIM_USERNAME = "username";
    private static final String TYPE_ACCESS    = "access";
    private static final String TYPE_REFRESH   = "refresh";

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");
    private static final int ACCESS_TOKEN_EXPIRY_HOURS  = 2;
    private static final int REFRESH_TOKEN_EXPIRY_DAYS  = 7;

    // ─────────────────────────────────────────────────────────────────────────

    @Value("${api.security.token.secret}")
    private String secret;

    // ── Geração ───────────────────────────────────────────────────────────────

    public String generateToken(User user) {
        return generateToken(String.valueOf(user.getId()), user.getUsername());
    }

    public String generateToken(String userIdSubject, String usernameClaim) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(userIdSubject)
                    .withClaim(CLAIM_USERNAME, usernameClaim)
                    .withClaim(CLAIM_TYPE, TYPE_ACCESS)
                    .withExpiresAt(accessTokenExpiry())
                    .sign(algorithm());
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar access token", e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(user.getId()))
                    .withClaim(CLAIM_USERNAME, user.getUsername())
                    .withClaim(CLAIM_TYPE, TYPE_REFRESH)
                    .withExpiresAt(refreshTokenExpiry())
                    .sign(algorithm());
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar refresh token", e);
        }
    }

    // ── Verificação ───────────────────────────────────────────────────────────

    public String getSubject(String accessToken) {
        return verifyAndDecode(accessToken, TYPE_ACCESS).getSubject();
    }

    public String getSubjectFromRefreshToken(String refreshToken) {
        return verifyAndDecode(refreshToken, TYPE_REFRESH).getSubject();
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        return verifyAndDecode(refreshToken, TYPE_REFRESH)
                .getClaim(CLAIM_USERNAME)
                .asString();
    }

    /** Retorna true se o token estiver expirado (assinatura válida, mas fora do prazo). */
    public boolean isExpired(String token) {
        try {
            JWT.require(algorithm()).build().verify(token);
            return false;
        } catch (TokenExpiredException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    /**
     * Verifica assinatura, issuer e type claim em uma única chamada.
     * Lança RuntimeException padronizada para qualquer falha de verificação.
     */
    private DecodedJWT verifyAndDecode(String token, String expectedType) {
        try {
            return JWT.require(algorithm())
                    .withIssuer(ISSUER)
                    .withClaim(CLAIM_TYPE, expectedType)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(
                    "Token inválido ou expirado [type=%s]".formatted(expectedType), e);
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant accessTokenExpiry() {
        return LocalDateTime.now()
                .plusHours(ACCESS_TOKEN_EXPIRY_HOURS)
                .toInstant(ZONE_OFFSET);
    }

    private Instant refreshTokenExpiry() {
        return LocalDateTime.now()
                .plusDays(REFRESH_TOKEN_EXPIRY_DAYS)
                .toInstant(ZONE_OFFSET);
    }
}
