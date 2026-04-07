package rinhacampusiv.api.v2.service.authentication;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.user.GeneratedAuthCookies;

@Service
public class CookieService {

    public GeneratedAuthCookies generateCookies(String accessToken, String refreshToken, boolean keepLoggedIn){

        int refreshTokenAge;

        if(keepLoggedIn){
            refreshTokenAge = 30 * 24 * 60 * 60 /* 30 dias */;
        }else {
            refreshTokenAge = 7 * 24 * 60 * 60 /* 7 dias */ ;
        }


        //  criar cookie HttpOnly
        ResponseCookie accessCookie = ResponseCookie.from("JWT", accessToken)
                .httpOnly(true)
                .secure(true)             // HTTPS obrigatório em produção
                .path("/")                // escopo do cookie
                .maxAge( 15 * 60)      // 15 minutos em segundos
                .sameSite("Lax")         // ajuda a mitigar CSRF (ajuste conforme seu domínio) -> Funciona com API e Front no mesmo dominio
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenAge)
                .sameSite("Lax")
                .build();

        return new GeneratedAuthCookies(accessCookie.toString(), refreshCookie.toString());
    }

    public GeneratedAuthCookies generateCleanCookies(){
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
                .path("/")  // deve ser o mesmo path do cookie original
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return new GeneratedAuthCookies(clearAccess.toString(), clearRefresh.toString());
    }
}
