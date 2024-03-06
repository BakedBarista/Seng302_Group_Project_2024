package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = EuropeanDecimalValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEuropeanDecimal {
    String message() default "Invalid decimal format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
