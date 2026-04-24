package rinhacampusiv.api.v2.validators.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EndsAtAfterStartsAtValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndsAtAfterStartsAt {
    int hours() default 1;

    String message() default "A data de término deve ser pelo menos {hours} hora(s) após a data de início";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
