package rinhacampusiv.api.v2.validators.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;

public class FutureByAtLeastHoursValidator
        implements ConstraintValidator<FutureByAtLeastHours, OffsetDateTime> {

    private int hours;

    @Override
    public void initialize(FutureByAtLeastHours annotation) {
        this.hours = annotation.value();
    }

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true; // deixa o @NotNull cuidar do null
        return value.isAfter(OffsetDateTime.now().plusHours(hours));
    }
}