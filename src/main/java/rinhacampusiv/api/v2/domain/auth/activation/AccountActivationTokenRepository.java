package rinhacampusiv.api.v2.domain.auth.activation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {

    Optional<AccountActivationToken> findByToken(String token);

    // Remove tokens anteriores do usuário antes de gerar um novo (reenvio de email)
    @Modifying
    @Query("DELETE FROM AccountActivationToken t WHERE t.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    // Limpeza periódica — pode ser chamado por um @Scheduled
    @Modifying
    @Query("DELETE FROM AccountActivationToken t WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredAndUsedTokens(@Param("now") OffsetDateTime now);
}