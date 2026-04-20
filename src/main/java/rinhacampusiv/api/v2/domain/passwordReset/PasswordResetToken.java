package rinhacampusiv.api.v2.domain.passwordReset;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rinhacampusiv.api.v2.domain.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public PasswordResetToken(User user) {
        this.token     = UUID.randomUUID().toString();
        this.user      = user;
        this.expiresAt = OffsetDateTime.now().plusMinutes(10); // expira em 10 minutos
        this.used      = false;
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !this.used && !this.isExpired();
    }

    public void markAsUsed() {
        this.used = true;
    }
}