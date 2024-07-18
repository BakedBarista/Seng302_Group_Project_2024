package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GardenSizeStringValidator implements ConstraintValidator<ValidGardenSizeString, String> {

    private int maxSize;

    @Override
    public void initialize(ValidGardenSizeString constraintAnnotation) {
        this.maxSize = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        double valueAsDouble = Double.parseDouble(value);

        return valueAsDouble < maxSize;
    }
}
