package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

public record CanceledTeamData(
        Long teamId,
        Long captainId,
        String captainUser,
        String name,
        TeamStatus status
) {

    public CanceledTeamData(Team team) {
        this(
                team.getId(),
                team.getCaptain().getId(),
                team.getCaptain().getUsername(),
                team.getName(),
                team.getStatus()
        );
    }
}
