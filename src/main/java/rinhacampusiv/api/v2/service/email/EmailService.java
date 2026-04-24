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
import rinhacampusiv.api.v2.infra.exception.SendEmailException;
import rinhacampusiv.api.v2.infra.loggers.EmailSenderLogger;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    @Autowired
    private EmailSenderLogger emailLogger;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        emailLogger.sendingPasswordResetEmailLog(toEmail, username);
        long start = System.currentTimeMillis();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String resetLink = frontendUrl + "/nova-senha?token=" + token;
            String html = templateService.buildResetPasswordTemplate(username, resetLink);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Rinha Campus IV — Recuperação de senha");
            helper.setText(html, true);

            mailSender.send(message);
            emailLogger.passwordResetEmailSentLog(toEmail, username, System.currentTimeMillis() - start);

        } catch (MessagingException e) {
            emailLogger.passwordResetEmailErrorLog(toEmail, username, e, System.currentTimeMillis() - start);
            throw new SendEmailException(e.getMessage());
        }
    }

    @Async
    public void sendAccountConfirmationEmail(String toEmail, String username, String token) {
        emailLogger.sendingAccountConfirmationEmailLog(toEmail, username);
        long start = System.currentTimeMillis();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String confirmationLink = frontendUrl + "/ativar-conta?token=" + token;
            String html = templateService.buildAccountActivationTemplate(username, confirmationLink);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Rinha Campus IV — Ative sua conta");
            helper.setText(html, true);

            mailSender.send(message);
            emailLogger.accountConfirmationEmailSentLog(toEmail, username, System.currentTimeMillis() - start);

        } catch (MessagingException e) {
            emailLogger.accountConfirmationEmailErrorLog(toEmail, username, e, System.currentTimeMillis() - start);
            throw new SendEmailException(e.getMessage());
        }
    }

    //TODO
    @Async
    public void sendPaymentConfirmationEmail(Team paymentTeam) {
        emailLogger.sendingPaymentConfirmationEmailLog(paymentTeam);
        long start = System.currentTimeMillis();

        try {
            var dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String toEmail = paymentTeam.getCaptain().getEmail();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            var paymentConfirmationInfo = new PaymentConfirmationInfo(
                    paymentTeam.getName(),
                    paymentTeam.getShieldUrl(),
                    paymentTeam.getTournament().getName(),
                    paymentTeam.getTournament().getStartsAt().format(dateFormat)
            );

            String html = templateService.buildPaymentConfirmationTemplate(paymentConfirmationInfo);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Rinha Campus IV — Inscrição confirmada");
            helper.setText(html, true);

            mailSender.send(message);
            emailLogger.paymentConfirmationEmailSentLog(paymentTeam, System.currentTimeMillis() - start);

        } catch (MessagingException e) {
            emailLogger.paymentConfirmationEmailErrorLog(paymentTeam, e, System.currentTimeMillis() - start);
            throw new SendEmailException(e.getMessage());
        }
    }
}
