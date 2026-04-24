package rinhacampusiv.api.v2.service.tournaments.payment.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PaymentExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(PaymentExpirationJob.class);

    @Autowired
    private PaymentExpirationService paymentExpirationService;

    @Scheduled(fixedRate = 120_000) // a cada 2 minutos
    public void checkExpiredPayments() {
        log.info("[JOB] Iniciando verificação de pagamentos expirados");
        long start = System.currentTimeMillis();

        try {
            paymentExpirationService.processExpiredPayments();
            log.info("[JOB] Verificação concluída | tempo={}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("[JOB] Erro inesperado durante verificação de pagamentos expirados | erro={} | tempo={}ms",
                    e.getMessage(), System.currentTimeMillis() - start);
        }
    }
}
