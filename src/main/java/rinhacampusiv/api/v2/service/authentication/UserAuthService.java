package rinhacampusiv.api.v2.service.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.user.GeneratedAuthCookies;
import rinhacampusiv.api.v2.domain.user.LoginData;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.infra.exception.AccountNotActivatedException;
import rinhacampusiv.api.v2.infra.exception.RefreshTokenNotFoundException;
import rinhacampusiv.api.v2.infra.security.TokenService;

import java.util.Arrays;


@Service
public class UserAuthService {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private AuthenticationManager manager;


    public GeneratedAuthCookies login(LoginData data) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = manager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        if (!user.isActive()) {
            throw new AccountNotActivatedException();
        }

        var accessToken = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);


        GeneratedAuthCookies authTokensCookies = cookieService.generateCookies(accessToken, refreshToken, data.keepLoggedIn());

        return authTokensCookies;

    }

    public GeneratedAuthCookies logout() {

        GeneratedAuthCookies cleanCookies = cookieService.generateCleanCookies();
        return cleanCookies;
    }

    public String refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "REFRESH".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token não encontrado"));

        String subject = tokenService.getSubjectFromRefreshToken(refreshToken);
        String username = tokenService.getClaimUsernameFromRefreshToken(refreshToken);


        String newAccessToken = tokenService.generateToken(subject, username);

        ResponseCookie newAccessCookie = ResponseCookie.from("JWT", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(15 * 60) //15 minutos
                .sameSite("Lax")
                .build();

        return newAccessCookie.toString();
    }


}
