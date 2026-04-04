package rinhacampusiv.api.v2.controller.tournaments;


import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.mercadoPago.MercadoPagoService;
import rinhacampusiv.api.v2.domain.mercadoPago.WebHookNotificationData;
import rinhacampusiv.api.v2.service.VerifyEfetuedPaymentService;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class paymentNotificationController {

    @Autowired
    private VerifyEfetuedPaymentService paymentChecker;

    @Autowired
    private MercadoPagoService mercadoPagoService;


    @PostMapping
    public ResponseEntity<Void> receivePayment(
            @RequestBody(required = false) WebHookNotificationData body,
            @RequestParam(required = false) Map<String, String> params
    ) {

        String paymentId = null;

        // Formato JSON (novo)
        if (body != null && body.data() != null) {
            System.out.println("[Webhook MP] JSON recebido: " + body.action());
            paymentId = body.data().id();
        }

        //Formato query param (legado)
        else if (params.containsKey("id")) {
            System.out.println("[Webhook MP] Query recebido");
            paymentId = params.get("id");
        }

        if (paymentId == null) {
            return ResponseEntity.ok().build();
        }

        Payment payment = mercadoPagoService.findPayment(paymentId);

        System.out.println("[Webhook MP] Status: " + payment.getStatus());

        if ("approved".equals(payment.getStatus())) {
            paymentChecker.verifyPayment(payment);
            System.out.println("[Webhook MP] Pagamento realizado: " + payment.getId());
        }

        return ResponseEntity.ok().build();
    }

}
