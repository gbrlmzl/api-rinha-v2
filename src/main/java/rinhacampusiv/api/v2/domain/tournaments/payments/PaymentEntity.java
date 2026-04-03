package rinhacampusiv.api.v2.domain.tournaments.payments;

import com.mercadopago.resources.payment.Payment;
import jakarta.persistence.*;
import lombok.*;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PaymentRegistrationDataMercadoPago;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento N:1 — cada equipe pode ter N Payments
    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false, unique = true)
    private Team team;

    @Column(name = "mercado_pago_id", unique = true)
    private String mercadoPagoId;

    @Column(nullable = false, unique = true, length = 50)
    private String uuid;

    @Column(nullable = false, length = 50)
    private String status; // PENDING, APPROVED, REJECTED, EXPIRED

    @Column(name = "status_detail", length = 100)
    private String statusDetail;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    // Preenchido quando status = APPROVED
    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(length = 100)
    private String payer;

    public PaymentEntity(Payment data) {
        this.mercadoPagoId = String.valueOf(data.getId());
        this.status       = data.getStatus();
        this.statusDetail = data.getStatusDetail();
        this.createdAt = data.getDateCreated();
        this.value = data.getTransactionAmount();

        this.uuid = UUID.randomUUID().toString();
        this.expiresAt    = data.getDateOfExpiration();
        this.payer        = data.getPayer().getFirstName() + " " +  data.getPayer().getLastName();
        this.qrCode = data.getPointOfInteraction().getTransactionData().getQrCode();

    }

    public void linkTeam (Team team){
        this.team = team;
    }



    /* Chamado quando o Mercado Pago confirma o pagamento (webhook/WebSocket)
    public void approve() {
        this.status = "APPROVED";
        this.paidAt = OffsetDateTime.now();
        this.team.activate(); // ativa a equipe automaticamente
    }

    public void reject(String detail) {
        this.status       = "REJECTED";
        this.statusDetail = detail;
    }

    public void expire() {
        this.status = "EXPIRED";
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }*/

}