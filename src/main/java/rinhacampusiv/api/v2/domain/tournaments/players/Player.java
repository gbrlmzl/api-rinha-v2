package rinhacampusiv.api.v2.domain.tournaments.players;


import jakarta.persistence.*;
import lombok.*;
import rinhacampusiv.api.v2.domain.tournaments.registrations.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.user.User;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // optional = true → permite NULL no banco (player sem usuário vinculado)
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(length = 100)
    private String name;

    @Column(name = "school_id")
    private String schoolId;

    @Column(name = "external_player", nullable = false)
    private boolean externalPlayer;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PlayerRole role;

    @Column(nullable = false)
    private String discord;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Player(PlayerRegisterData data, Team team){
        this.name = data.playerName();
        if(data.schoolId().trim().isEmpty()){
            this.schoolId = null;
        }else{
            this.schoolId = data.schoolId();
        }
        this.nickname = data.nickname();
        this.discord = data.discord();
        this.role = data.role();
        this.externalPlayer = data.externalPlayer();
        this.team = team;

        this.active = false;
    }

    public void updateData(PlayerUpdateData updateDto){
        if(updateDto.discord() != null){
            this.discord = updateDto.discord();
        }
        if(updateDto.nickname() != null){
            this.nickname = updateDto.nickname();
        }

    }

    public void claimPlayer(User user) {
        if (this.user != null) {
            throw new IllegalStateException("Jogador já vinculado a um usuário");
        }
        this.user = user;
    }

}


