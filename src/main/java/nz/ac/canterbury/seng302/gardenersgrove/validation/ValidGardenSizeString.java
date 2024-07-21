package nz.ac.canterbury.seng302.gardenersgrove.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.MAX_GARDEN_SIZE;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.MAX_GARDEN_SIZE_MESSAGE;

/**
 * Interface for using GardenSizeStringValidator in the GardenDTO class
 */
@Constraint(validatedBy = GardenSizeStringValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGardenSizeString {
    int max() default MAX_GARDEN_SIZE;
    String message() default MAX_GARDEN_SIZE_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
