package nz.ac.canterbury.seng302.gardenersgrove.customValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Date is not in valid format, DD/MM/YYYY, or does not represent a real date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}