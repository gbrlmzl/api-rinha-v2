package rinhacampusiv.api.v2.domain.tournaments.payments;

import com.mercadopago.resources.payment.Payment;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "mercado_pago_id", unique = true)
    private String mercadoPagoId;

    @Column(nullable = false, unique = true, length = 50)
    private String uuid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

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


    /* Construtor legado */
    public PaymentEntity(Payment data) {
        this.mercadoPagoId = String.valueOf(data.getId());
        this.status = PaymentStatus.fromMercadoPago(data.getStatus());
        this.statusDetail = data.getStatusDetail();
        this.createdAt = data.getDateCreated();
        this.value = data.getTransactionAmount();

        this.uuid = UUID.randomUUID().toString();
        this.expiresAt    = data.getDateOfExpiration();
        this.qrCode = data.getPointOfInteraction().getTransactionData().getQrCode();

    }

    public PaymentEntity(Payment data, String payerName) {
        this.mercadoPagoId = String.valueOf(data.getId());
        this.status = PaymentStatus.fromMercadoPago(data.getStatus());
        this.statusDetail = data.getStatusDetail();
        this.createdAt = data.getDateCreated();
        this.value = data.getTransactionAmount();

        this.uuid = UUID.randomUUID().toString();
        this.expiresAt    = data.getDateOfExpiration();
        this.qrCode = data.getPointOfInteraction().getTransactionData().getQrCode();
        this.payer = payerName;

    }

    public void linkTeam (Team team){
        this.team = team;
    }

    public void approve(OffsetDateTime paidAt, String statusDetail) {
        this.status = PaymentStatus.approved;
        this.statusDetail = statusDetail;
        this.paidAt = paidAt;
    }

    public void expire() {
        this.status = PaymentStatus.expired;
        this.statusDetail = "expired";
    }

    public boolean isPending() {
        return this.status == PaymentStatus.pending;
    }

    

    public boolean isApproved() {
        return this.status == PaymentStatus.approved;
    }

}