package rinhacampusiv.api.v2.domain.tournaments.teams;

import com.mercadopago.resources.payment.Payment;
import jakarta.persistence.*;
import lombok.*;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.payments.PaymentEntity;
import rinhacampusiv.api.v2.domain.tournaments.players.Player;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.user.User;

import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"players", "payment"})
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "captain_id", nullable = false)
    private User captain;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "shield_url")
    private String shieldUrl;

    @Column(nullable = false, length = 30)
    private TeamStatus status; // PENDING_PAYMENT, ACTIVE

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments  = new ArrayList<>();

    public Team(TeamRegisterData data, User captain, Tournament tournament) {
        this.captain    = captain;
        this.tournament = tournament;
        this.name       = data.teamName();
        this.status     = TeamStatus.PENDING_PAYMENT;
        this.active     = false;
        this.players    = convertPlayerList(data.players(), this);
    }

    private List<Player> convertPlayerList(List<PlayerRegisterData> list, Team team) {
        return Optional.ofNullable(list)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(data -> new Player(data, team))
                .toList();
    }

    public void paymentGenerated(PaymentEntity payment) {
        this.payments.add(payment);
    }

    public void approvedPayment(){
        this.active = true;
        this.status = TeamStatus.READY;
        this.players.forEach(player -> player.setActive(true));
    }

    public void updateData(TeamUpdateData data) {
        if (data.teamName() != null)      this.name      = data.teamName();
        if (data.shieldUrl() != null) this.shieldUrl = data.shieldUrl();
    }

    public boolean isPendingPayment() {
        return "PENDING_PAYMENT".equals(this.status);
    }
}
