package rinhacampusiv.api.v2.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import rinhacampusiv.api.v2.domain.tournaments.registrations.response.PaymentConfirmationInfo;

@Service
public class EmailTemplateService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String buildResetPasswordTemplate(String username, String resetLink) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("resetLink", resetLink);
        return templateEngine.process("emails/reset-password", context);
    }

    public String buildAccountActivationTemplate(String username, String confirmationLink) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("confirmationLink", confirmationLink);
        return templateEngine.process("emails/account-activation", context);
    }

    public String buildPaymentConfirmationTemplate(PaymentConfirmationInfo paymentConfirmationInfo) {
        Context context = new Context();
        context.setVariable("teamName", paymentConfirmationInfo.teamName());
        context.setVariable("shieldUrl", paymentConfirmationInfo.shieldUrl());
        context.setVariable("tournamentName", paymentConfirmationInfo.tournamentName());
        context.setVariable("tournamentStartDate", paymentConfirmationInfo.tournamentStartDate());
        return templateEngine.process("emails/confirm-payment", context);
    }
}