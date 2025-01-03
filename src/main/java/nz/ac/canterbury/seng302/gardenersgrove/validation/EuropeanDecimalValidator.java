package nz.ac.canterbury.seng302.gardenersgrove.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class EuropeanDecimalValidator implements ConstraintValidator<ValidEuropeanDecimal, String> {

    @Override
    public void initialize(ValidEuropeanDecimal constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        } else if (value.contains("e")) {
            return false;
        }

        try {
            String standardizedValue = value.replace(',', '.');
            double numericValue = Double.parseDouble(standardizedValue);
            return numericValue >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}