package rinhacampusiv.api.v2.domain.userAuthProvider;
import rinhacampusiv.api.v2.domain.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;

@Table(
        name = "user_auth_providers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerId"})
)
@Entity(name = "UserAuthProvider")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserAuthProvider {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(nullable = false, length = 100)
    private String providerId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}