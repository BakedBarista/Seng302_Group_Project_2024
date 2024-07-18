package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Class for validating that a given string is smaller than a predefined maxSize.
 */
public class GardenSizeStringValidator implements ConstraintValidator<ValidGardenSizeString, String> {

    private int maxSize;

    @Override
    public void initialize(ValidGardenSizeString constraintAnnotation) {
        this.maxSize = constraintAnnotation.max();
    }

    /**
     * Compares the given string to the defined max, if it is less than the max, return true.
     * NOTE: given string MUST be able to be parsed to a double, which the @ValidEuropeanDecimal validator checks.
     *
     * @param value object to validate - MUST be able to be parsed as a double
     * @param context context in which the constraint is evaluated
     *
     * @return true if less than max
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank() || value.contains("e")) {
            return true;
        }

        try {
            double valueAsDouble = Double.parseDouble(value);
            return valueAsDouble <= maxSize;
        } catch (Exception e){
            return true;
        }
    }
}
