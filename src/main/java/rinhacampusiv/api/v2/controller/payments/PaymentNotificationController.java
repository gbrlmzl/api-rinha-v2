package rinhacampusiv.api.v2.controller.payments;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rinhacampusiv.api.v2.infra.external.mercadopago.WebHookNotificationData;
import rinhacampusiv.api.v2.service.tournaments.payment.PaymentWebhookService;

@RestController
@RequestMapping("/webhook")
public class PaymentNotificationController {

    @Autowired
    private PaymentWebhookService paymentWebhookService;


    @PostMapping
    public ResponseEntity<Void> receivePayment(
            @RequestBody WebHookNotificationData body) {

        paymentWebhookService.processPayment(body);
        return ResponseEntity.ok().build();
    }

}
