package rinhacampusiv.api.v2.validators.tournament.team.register;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.PlayerRegisterData;
import rinhacampusiv.api.v2.domain.tournaments.registrations.request.TournamentRegistrationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.infra.exception.ValidatorException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TournamentTeamRegisterValidatorSameSchoolId implements TournamentTeamRegisterValidator {
    @Override
    public void validate (TournamentRegistrationData data, Tournament tournament){
        List<PlayerRegisterData> players = data.teamData().players();
        Set<String> uniques = new HashSet<>();
        boolean hasDuplicates = players.stream()
                .filter(player -> player.schoolId() != null && !player.schoolId().isBlank() )
                .anyMatch(player -> !uniques.add(player.schoolId()));

        if(hasDuplicates){
            throw new ValidatorException("Não é permitido o cadastro de jogadores com a mesma matrícula");
        }

    }
}
