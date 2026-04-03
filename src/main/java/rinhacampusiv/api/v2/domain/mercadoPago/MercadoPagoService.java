package rinhacampusiv.api.v2.domain.mercadoPago;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.test.token}")
    private String accessTokenTest;

    private PaymentClient paymentClient;

    public void init(){
        this.paymentClient = new PaymentClient();
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Payment findPayment(String mercadoPagoId){
        try{
            init();
            return paymentClient.get(Long.valueOf(mercadoPagoId));
        } catch (MPException | MPApiException e){
            throw new RuntimeException("Erro ao consultar pagamento: " + e.getMessage());
        }
    }
}
