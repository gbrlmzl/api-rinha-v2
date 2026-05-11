package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private TournamentGame game;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TournamentStatus status;

    @Column(name = "max_teams")
    private Integer maxTeams;

    @Column(name = "prize_pool")
    private BigDecimal prizePool;

    @Column(name = "starts_at")
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rules_url")
    private String rulesUrl;

    @Column(unique = true)
    private String slug;

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null) this.slug = toSlug(this.name);
    }

    public static String toSlug(String text) {
        if (text == null) return null;
        // NFD decompõe caracteres compostos:  ç → c + cedilha,  ã → a + til,  é → e + acento, etc.
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{M}", "")   // remove TODAS as marcas de combinação (acentos, cedilha, til…)
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")  // remove tudo que não é letra, número ou espaço
                .trim()
                .replaceAll("\\s+", "-")   // espaços → hífens
                .replaceAll("-+", "-");    // hífens duplos → um único
    }

    public Tournament(@Valid TournamentCreationData tournamentDto) {
        this.name = tournamentDto.name();
        this.game = tournamentDto.game();
        this.status = TournamentStatus.OPEN;
        this.maxTeams = tournamentDto.maxTeams();
        this.prizePool = tournamentDto.prizePool();
        this.startsAt = tournamentDto.startsAt();
        this.endsAt = tournamentDto.endsAt();
        this.createdAt = OffsetDateTime.now();
        this.description = tournamentDto.description();
        this.rulesUrl = tournamentDto.rulesUrl();
    }

    public void updateInformation(TournamentUpdateData data) {
        if (data.name() != null) this.name = data.name();
        if (data.game() != null) this.game = data.game();
        if (data.maxTeams() != null) this.maxTeams = data.maxTeams();
        if (data.prizePool() != null) this.prizePool = data.prizePool();
        if (data.startsAt() != null) this.startsAt = data.startsAt();
        if (data.endsAt() != null) this.endsAt = data.endsAt();
        if (data.description() != null) this.description = data.description();
        if (data.imageUrl() != null) this.imageUrl = data.imageUrl();
        if (data.status() != null) this.status = data.status();
        if (data.status() == TournamentStatus.FINISHED || data.status() == TournamentStatus.CANCELED) this.endsAt = OffsetDateTime.now();
        if (data.rulesUrl() != null) this.rulesUrl = data.rulesUrl();
    }

}
