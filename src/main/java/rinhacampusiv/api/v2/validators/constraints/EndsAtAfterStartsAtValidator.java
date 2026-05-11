package rinhacampusiv.api.v2.validators.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;

public class EndsAtAfterStartsAtValidator implements ConstraintValidator<EndsAtAfterStartsAt, TournamentCreationData> {

    private int hours;

    @Override
    public void initialize(EndsAtAfterStartsAt annotation) {
        this.hours = annotation.hours();
    }

    @Override
    public boolean isValid(TournamentCreationData data, ConstraintValidatorContext context) {
        if (data == null || data.startsAt() == null || data.endsAt() == null) {
            return true; 
        }

        boolean isValid = data.endsAt().isAfter(data.startsAt().plusHours(hours));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endsAt")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
