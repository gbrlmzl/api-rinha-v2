package rinhacampusiv.api.v2.controller.payments;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.mercadoPago.WebHookNotificationData;
import rinhacampusiv.api.v2.service.WebhookPaymentService;

@RestController
@RequestMapping("/webhook")
public class PaymentNotificationController {

    @Autowired
    private WebhookPaymentService webhookService;


    @PostMapping
    public ResponseEntity<Void> receivePayment(
            @RequestBody WebHookNotificationData body) {

        webhookService.processPayment(body);
        return ResponseEntity.ok().build();
    }

}
