package nz.ac.canterbury.seng302.gardenersgrove.customValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants.MAX_GARDEN_SIZE;
import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationMessages.MAX_GARDEN_SIZE_MESSAGE;

@Constraint(validatedBy = GardenSizeStringValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGardenSizeString {
    int max() default MAX_GARDEN_SIZE;
    String message() default MAX_GARDEN_SIZE_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
