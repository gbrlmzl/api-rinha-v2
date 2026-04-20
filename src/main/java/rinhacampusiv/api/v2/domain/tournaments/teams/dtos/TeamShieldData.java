package rinhacampusiv.api.v2.domain.tournaments.teams.dtos;

import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.validators.tournament.team.register.ValidTeamShield;

public record TeamShieldData(
        @ValidTeamShield
        MultipartFile teamShield
) {
}
