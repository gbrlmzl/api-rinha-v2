package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.teams.Team;

import java.util.List;

public record TeamPublicData(
        Long id,
        String name,
        String shieldUrl,
        String captainNickname,
        List<PlayerPublicData> players
){
    public TeamPublicData(Team team) {
        this(
            team.getId(),
            team.getName(),
            team.getShieldUrl(),
            team.getCaptain() != null ? team.getCaptain().getNickname() : null,
            team.getPlayers() != null
                ? team.getPlayers().stream()
                    .map(player -> new PlayerPublicData(player.getNickname(), player.getRole()))
                    .toList()
                : List.of()
        );
    }
}
