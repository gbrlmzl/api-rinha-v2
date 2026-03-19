package rinhacampusiv.api.v2.infra.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rinhacampusiv.api.v2.domain.user.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;


    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/auth/me"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

       /*
        if (path.equals("/auth/login") || path.equals("/auth/register")) { //alteração temporária para ignorar as rotas públicas para teste com inmsomnia
            filterChain.doFilter(request, response);
            return;
        }
        */

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try{
                var subject = tokenService.getSubject(tokenJWT);
                var user = repository.findByUsername(subject);

                if(user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // token inválido → apenas não autentica
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);

    }

    private String recuperarToken(HttpServletRequest request) {
        // 1) Tenta pegar do header Authorization
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer", "").trim();
        }

        // 2) Se não tiver header, tenta pegar do cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
