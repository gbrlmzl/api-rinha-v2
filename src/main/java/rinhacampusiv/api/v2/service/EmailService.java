package rinhacampusiv.api.v2.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.infra.exception.SendPasswordResetEmailException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
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

        } catch (MessagingException e) {
            throw new SendPasswordResetEmailException(e.getMessage());
        }
    }

    @Async
    public void sendAccountConfirmationEmail(String toEmail, String username, String token) {
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

        } catch (MessagingException e) {
            throw new SendPasswordResetEmailException(e.getMessage());
        }
    }
}