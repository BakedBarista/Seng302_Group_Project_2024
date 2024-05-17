package nz.ac.canterbury.seng302.gardenersgrove.customValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Date must be in the format DD/MM/YYYY";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}