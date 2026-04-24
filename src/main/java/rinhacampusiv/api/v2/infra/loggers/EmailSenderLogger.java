package rinhacampusiv.api.v2.infra.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;

@Component
public class EmailSenderLogger {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderLogger.class);


    // ─── Recuperação de senha ───────────────────────────────────────────────────

    public void sendingPasswordResetEmailLog(String toEmail, String username) {
        logger.info("[EMAIL] Enviando email de recuperação de senha | destinatário={} | usuário={}",
                maskEmail(toEmail), username);
    }

    public void passwordResetEmailSentLog(String toEmail, String username) {
        logger.info("[EMAIL] Email de recuperação de senha enviado com sucesso | destinatário={} | usuário={}",
                maskEmail(toEmail), username);
    }

    public void passwordResetEmailErrorLog(String toEmail, String username, Exception e) {
        logger.error("[EMAIL] Falha ao enviar email de recuperação de senha | destinatário={} | usuário={} | erro={}",
                maskEmail(toEmail), username, e.getMessage());
    }


    // ─── Confirmação de conta ───────────────────────────────────────────────────

    public void sendingAccountConfirmationEmailLog(String toEmail, String username) {
        logger.info("[EMAIL] Enviando email de ativação de conta | destinatário={} | usuário={}",
                maskEmail(toEmail), username);
    }

    public void accountConfirmationEmailSentLog(String toEmail, String username) {
        logger.info("[EMAIL] Email de ativação de conta enviado com sucesso | destinatário={} | usuário={}",
                maskEmail(toEmail), username);
    }

    public void accountConfirmationEmailErrorLog(String toEmail, String username, Exception e) {
        logger.error("[EMAIL] Falha ao enviar email de ativação de conta | destinatário={} | usuário={} | erro={}",
                maskEmail(toEmail), username, e.getMessage());
    }


    // ─── Confirmação de pagamento ───────────────────────────────────────────────

    public void sendingPaymentConfirmationEmailLog(Team team) {
        logger.info("[EMAIL] Enviando email de confirmação de inscrição | destinatário={} | equipe={} | torneio={}",
                maskEmail(team.getCaptain().getEmail()),
                team.getName(),
                team.getTournament().getName());
    }

    public void paymentConfirmationEmailSentLog(Team team) {
        logger.info("[EMAIL] Email de confirmação de inscrição enviado com sucesso | destinatário={} | equipe={} | torneio={}",
                maskEmail(team.getCaptain().getEmail()),
                team.getName(),
                team.getTournament().getName());
    }

    public void paymentConfirmationEmailErrorLog(Team team, Exception e) {
        logger.error("[EMAIL] Falha ao enviar email de confirmação de inscrição | destinatário={} | equipe={} | torneio={} | erro={}",
                maskEmail(team.getCaptain().getEmail()),
                team.getName(),
                team.getTournament().getName(),
                e.getMessage());
    }


    // ─── Utilitário ────────────────────────────────────────────────────────────

    /**
     * Mascara o endereço de e-mail para evitar exposição de dados pessoais nos logs.
     * Exemplo: joao.silva@email.com → j********a@email.com
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        int atIndex = email.indexOf('@');
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) return "**" + domain;
        return local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1) + domain;
    }
}
