package rinhacampusiv.api.v2.controller.payments;


import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.mercadoPago.MercadoPagoService;
import rinhacampusiv.api.v2.domain.mercadoPago.WebHookNotificationData;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentWebhookLogRepository;
import rinhacampusiv.api.v2.service.VerifyEfetuedPaymentService;
import rinhacampusiv.api.v2.service.WebhookPaymentService;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class PaymentNotificationController {

    @Autowired
    private WebhookPaymentService webhookService;


    @PostMapping
    public ResponseEntity<Void> receivePayment(
            @RequestBody WebHookNotificationData body) {

        webhookService.process(body);
        return ResponseEntity.ok().build();
    }

}
