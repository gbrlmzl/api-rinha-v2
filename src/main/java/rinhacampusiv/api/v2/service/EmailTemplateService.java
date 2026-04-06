package rinhacampusiv.api.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

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
}