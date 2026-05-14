package rinhacampusiv.api.v2.infra.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que injeta um ID único em cada requisição HTTP via MDC (Mapped Diagnostic Context).
 *
 * O requestId aparece automaticamente em todos os logs daquela requisição,
 * permitindo rastrear toda a cadeia de eventos de uma operação mesmo quando
 * múltiplos usuários estão sendo atendidos simultaneamente.
 *
 * Exemplo de saída no console:
 *   10:45:01.123 INFO  [a3f9b2c1] EmailSenderLogger : [EMAIL] Enviando email...
 *   10:45:01.890 INFO  [a3f9b2c1] EmailSenderLogger : [EMAIL] Email enviado | tempo=767ms
 */
@Component
@Order(1)
public class MdcFilter implements Filter {

    private static final String REQUEST_ID_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID_KEY, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
