package rinhacampusiv.api.v2.validators.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureByAtLeastHoursValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureByAtLeastHours {

    int value() default 1;

    String message() default "A data deve ser pelo menos {value} hora(s) no futuro";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}