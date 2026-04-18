package rinhacampusiv.api.v2.controller.payments;


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
public class PaymentNotificationController {

    @Autowired
    private VerifyEfetuedPaymentService paymentChecker;

    @Autowired
    private MercadoPagoService mercadoPagoService;


    @PostMapping
    public ResponseEntity<Void> receivePayment(@RequestBody WebHookNotificationData body) {

        if (body == null || body.data() == null || body.data().id() == null) {
            // Sem dados válidos, apenas retorna OK para evitar retries
            return ResponseEntity.ok().build();
        }

        String paymentId = body.data().id();

        Payment payment = mercadoPagoService.findPayment(paymentId, true);

        if (payment == null) {
            // Payment não encontrado, retorna OK para o Mercado Pago parar de reenviar
            return ResponseEntity.ok().build();
        }

        System.out.println("Payment status: " + payment.getStatus());
        System.out.println("Payment status detail: " + payment.getStatusDetail());

        if(payment.getStatus().equals("pending") ){
            return ResponseEntity.ok().build();
        }

        paymentChecker.updatePayment(payment);

        return  ResponseEntity.ok().build();


        /*if ("approved".equalsIgnoreCase(payment.getStatus())) {
            paymentChecker.verifyPayment(payment);
            System.out.println("[Webhook MP] Pagamento realizado: " + payment.getId());
        } else if ("expired".equalsIgnoreCase(payment.getStatus())) {
            // Aqui você pode atualizar o PaymentEntity e o TeamStatus para "EXPIRED"
            //paymentChecker.updatePaymentStatus(payment);
        }*/
    }


}
