package rinhacampusiv.api.v2.service.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.auth.GeneratedAuthCookies;

@Service
public class CookieService {

    @Value("${app.cookies.secure}")
    private boolean secureCookies;

    public GeneratedAuthCookies generateCookies(String accessToken, String refreshToken, boolean keepLoggedIn){

        int refreshTokenAge;

        if(keepLoggedIn){
            refreshTokenAge = 30 * 24 * 60 * 60 /* 30 dias */;
        }else {
            refreshTokenAge = 7 * 24 * 60 * 60 /* 7 dias */ ;
        }


        ResponseCookie accessCookie = ResponseCookie.from("JWT", accessToken)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge( 15 * 60)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", refreshToken)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(refreshTokenAge)
                .sameSite("Lax")
                .build();

        return new GeneratedAuthCookies(accessCookie.toString(), refreshCookie.toString());
    }

    public GeneratedAuthCookies generateCleanCookies(){
        ResponseCookie clearAccess = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("REFRESH", "")
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return new GeneratedAuthCookies(clearAccess.toString(), clearRefresh.toString());
    }

    public ResponseCookie buildAccessCookie(String accessToken) {
        return ResponseCookie.from("JWT", accessToken)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();
    }
}
