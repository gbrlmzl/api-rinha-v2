package rinhacampusiv.api.v2.validators.tournament.team.register;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TeamShieldValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTeamShield {
    String message() default "O escudo deve ter até 10MB e estar em um formato válido (PNG, JPG, GIF)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}