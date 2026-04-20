package rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos;

import rinhacampusiv.api.v2.domain.tournaments.players.PlayerRole;

public record PlayerPublicData(
        String nickname,
        PlayerRole role
) {
}
