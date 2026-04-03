package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
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


    public Tournament(@Valid TournamentCreationData tournamentDto) {
        this.name = tournamentDto.name();
        this.game = tournamentDto.game();
        this.status = TournamentStatus.OPEN;
        this.maxTeams = tournamentDto.maxTeams();
        this.prizePool = tournamentDto.prizePool();
        this.startsAt = tournamentDto.startsAt();
        this.endsAt = tournamentDto.endsAt();
    }
}
