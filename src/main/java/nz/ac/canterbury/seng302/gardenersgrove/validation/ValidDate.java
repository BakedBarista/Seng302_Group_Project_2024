package nz.ac.canterbury.seng302.gardenersgrove.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Date is not in valid format, DD/MM/YYYY, or is not within the valid range of dates (1901 - present)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}