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
                toEmail, username);
    }

    public void passwordResetEmailSentLog(String toEmail, String username, long elapsedMs) {
        logger.info("[EMAIL] Email de recuperação de senha enviado com sucesso | destinatário={} | usuário={} | tempo={}ms",
                toEmail, username, elapsedMs);
    }

    public void passwordResetEmailErrorLog(String toEmail, String username, Exception e, long elapsedMs) {
        logger.error("[EMAIL] Falha ao enviar email de recuperação de senha | destinatário={} | usuário={} | erro={} | tempo={}ms",
                toEmail, username, e.getMessage(), elapsedMs);
    }


    // ─── Confirmação de conta ───────────────────────────────────────────────────

    public void sendingAccountConfirmationEmailLog(String toEmail, String username) {
        logger.info("[EMAIL] Enviando email de ativação de conta | destinatário={} | usuário={}",
                toEmail, username);
    }

    public void accountConfirmationEmailSentLog(String toEmail, String username, long elapsedMs) {
        logger.info("[EMAIL] Email de ativação de conta enviado com sucesso | destinatário={} | usuário={} | tempo={}ms",
                toEmail, username, elapsedMs);
    }

    public void accountConfirmationEmailErrorLog(String toEmail, String username, Exception e, long elapsedMs) {
        logger.error("[EMAIL] Falha ao enviar email de ativação de conta | destinatário={} | usuário={} | erro={} | tempo={}ms",
                toEmail, username, e.getMessage(), elapsedMs);
    }


    // ─── Confirmação de pagamento ───────────────────────────────────────────────

    public void sendingPaymentConfirmationEmailLog(Team team) {
        logger.info("[EMAIL] Enviando email de confirmação de inscrição | destinatário={} | equipe={} | torneio={}",
                team.getCaptain().getEmail(),
                team.getName(),
                team.getTournament().getName());
    }

    public void paymentConfirmationEmailSentLog(Team team, long elapsedMs) {
        logger.info("[EMAIL] Email de confirmação de inscrição enviado com sucesso | destinatário={} | equipe={} | torneio={} | tempo={}ms",
                team.getCaptain().getEmail(),
                team.getName(),
                team.getTournament().getName(),
                elapsedMs);
    }

    public void paymentConfirmationEmailErrorLog(Team team, Exception e, long elapsedMs) {
        logger.error("[EMAIL] Falha ao enviar email de confirmação de inscrição | destinatário={} | equipe={} | torneio={} | erro={} | tempo={}ms",
                team.getCaptain().getEmail(),
                team.getName(),
                team.getTournament().getName(),
                e.getMessage(),
                elapsedMs);
    }



}
