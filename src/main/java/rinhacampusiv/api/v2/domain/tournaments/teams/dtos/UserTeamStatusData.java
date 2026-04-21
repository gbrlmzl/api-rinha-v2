package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.players.PlayerPublicData;

import java.util.List;

public record UserTeamStatusData(
        TeamStatus teamStatus,
        String teamName,
        String teamShieldUrl,
        List<PlayerPublicData> players

) { public UserTeamStatusData(Team team){
    this(
            team.getStatus(),
            team.getName(),
            team.getShieldUrl(),
            team.getPlayers().stream()
                    .map(player -> new PlayerPublicData(player.getNickname(), player.getRole()))
                    .toList()
    );
}
}
