package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;

public record TeamDataUpdateDTO(
        Boolean cancelRegistration
) {
}
