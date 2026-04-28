package rinhacampusiv.api.v2.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.PaymentConfirmationInfo;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.infra.exception.tournaments.SendEmailException;
import rinhacampusiv.api.v2.infra.loggers.EmailSenderLogger;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

@Service
public class EmailService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired private JavaMailSender mailSender;
    @Autowired private EmailTemplateService templateService;
    @Autowired private EmailSenderLogger emailLogger;

    @Value("${spring.mail.username}") private String fromEmail;
    @Value("${app.frontend.url}")     private String frontendUrl;

    // ── Casos de uso ──────────────────────────────────────────────────────────

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        String resetLink = frontendUrl + "/nova-senha?token=" + token;
        String html      = templateService.buildResetPasswordTemplate(username, resetLink);

        send(new EmailRequest(
                toEmail,
                "Rinha da UFPB — Recuperação de senha",
                html,
                () -> emailLogger.sendingPasswordResetEmailLog(toEmail, username),
                elapsed -> emailLogger.passwordResetEmailSentLog(toEmail, username, elapsed),
                (e, elapsed) -> emailLogger.passwordResetEmailErrorLog(toEmail, username, e, elapsed)
        ));
    }

    @Async
    public void sendAccountConfirmationEmail(String toEmail, String username, String token) {
        String confirmationLink = frontendUrl + "/ativar-conta?token=" + token;
        String html             = templateService.buildAccountActivationTemplate(username, confirmationLink);

        send(new EmailRequest(
                toEmail,
                "Rinha da UFPB — Ative sua conta",
                html,
                () -> emailLogger.sendingAccountConfirmationEmailLog(toEmail, username),
                elapsed -> emailLogger.accountConfirmationEmailSentLog(toEmail, username, elapsed),
                (e, elapsed) -> emailLogger.accountConfirmationEmailErrorLog(toEmail, username, e, elapsed)
        ));
    }

    @Async
    public void sendPaymentConfirmationEmail(Team team) {
        var info = new PaymentConfirmationInfo(
                team.getName(),
                team.getShieldUrl(),
                team.getTournament().getName(),
                team.getTournament().getStartsAt().format(DATE_FORMAT)
        );
        String html = templateService.buildPaymentConfirmationTemplate(info);

        send(new EmailRequest(
                team.getCaptain().getEmail(),
                "Rinha da UFPB — Inscrição confirmada",
                html,
                () -> emailLogger.sendingPaymentConfirmationEmailLog(team),
                elapsed -> emailLogger.paymentConfirmationEmailSentLog(team, elapsed),
                (e, elapsed) -> emailLogger.paymentConfirmationEmailErrorLog(team, e, elapsed)
        ));
    }

    // ── Infraestrutura de envio ───────────────────────────────────────────────

    /**
     * Executa o envio de um e-mail, medindo tempo e delegando logs ao chamador.
     * Toda a duplicação de MimeMessage, MimeMessageHelper e tratamento de erro
     * fica aqui — os métodos públicos só lidam com conteúdo.
     */
    private void send(EmailRequest request) {
        request.onStart().run();
        long start = System.currentTimeMillis();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(request.to());
            helper.setSubject(request.subject());
            helper.setText(request.html(), true);

            mailSender.send(message);
            request.onSuccess().accept(System.currentTimeMillis() - start);

        } catch (MessagingException e) {
            request.onError().accept(e, System.currentTimeMillis() - start);
            throw new SendEmailException(e.getMessage());
        }
    }

    /**
     * Agrupa os dados e callbacks de um envio de e-mail.
     *
     */
    private record EmailRequest(
            String to,
            String subject,
            String html,
            Runnable onStart,
            LongConsumer onSuccess,
            BiConsumer<MessagingException, Long> onError
    ) {}
}