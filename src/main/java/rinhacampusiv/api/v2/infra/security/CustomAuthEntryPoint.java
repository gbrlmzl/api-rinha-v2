package rinhacampusiv.api.v2.infra.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

// infra/security/CustomAuthEntryPoint.java
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private TokenService tokenService;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String tokenError = (String) request.getAttribute("token_error");
        String error = tokenError != null ? tokenError : "unauthorized";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // sempre 401
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + error + "\"}");
    }
}