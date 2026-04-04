package rinhacampusiv.api.v2.domain.tournaments.teams;

import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.validators.ValidTeamShield;

public record TeamShieldData(
        @ValidTeamShield
        MultipartFile teamShield
) {
}
