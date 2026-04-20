package rinhacampusiv.api.v2.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("API Rinha.v2")
                    .withSubject(String.valueOf(usuario.getId()))
                    .withClaim("username", usuario.getUsername())
                    .withClaim("type", "access")
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    public String generateToken(String userIdSubject, String usernameClaim){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("API Rinha.v2")
                    .withSubject(userIdSubject)
                    .withClaim("username", usernameClaim)
                    .withClaim("type", "access")
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }

    }


    public String generateRefreshToken(User usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("API Rinha.v2")
                    .withSubject(String.valueOf(usuario.getId()))
                    .withClaim("type", "refresh") // <- importante
                    .withClaim("username", usuario.getUsername())
                    .withExpiresAt(dataExpiracaoRefresh())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar refresh token", exception);
        }
    }


    public String getSubject(String tokenJWT){
        try {
            var algorithm = Algorithm.HMAC256(secret);

            var decoded =  JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer("API Rinha.v2")
                    .withClaim("type", "access")
                    // reusable verifier instance
                    .build()
                    .verify(tokenJWT);


            return decoded.getSubject();

        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }

    }



    public String getSubjectFromRefreshToken(String refreshToken) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            var decoded = JWT.require(algorithm)
                    .withIssuer("API Rinha.v2")
                    .withClaim("type", "refresh") // <- garante que é refresh
                    .build()
                    .verify(refreshToken);


            return decoded.getSubject();

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Refresh token inválido ou expirado!");
        }
    }

    public String getClaimUsernameFromRefreshToken(String refreshToken) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            var decoded = JWT.require(algorithm)
                    .withIssuer("API Rinha.v2")
                    .withClaim("type", "refresh") // <- garante que é refresh
                    .build()
                    .verify(refreshToken);


            return decoded.getClaim("username").asString();

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Refresh token inválido ou expirado!");
        }


    }

    // Retorna true se o token existir mas estiver expirado
    public boolean isExpired(String token) {
        var algorithm = Algorithm.HMAC256(secret);

        try {
            JWT.require(algorithm).build().verify(token);
            return false; // válido
        } catch (TokenExpiredException e) {
            return true;  // expirado — vale tentar refresh
        } catch (Exception e) {
            return false; // inválido por outro motivo
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant dataExpiracaoRefresh() {
        return LocalDateTime.now()
                .plusDays(7) // 👈 refresh dura mais
                .toInstant(ZoneOffset.of("-03:00"));
    }


}
