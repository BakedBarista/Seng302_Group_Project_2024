package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class EuropeanDecimalValidator implements ConstraintValidator<ValidEuropeanDecimal, String> {

    @Override
    public void initialize(ValidEuropeanDecimal constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // valid if optional
        }
        try {
            String standardizedValue = value.replace(',', '.');
            double numericValue = Double.parseDouble(standardizedValue);
            return numericValue >= 1; // Replace 1 with any minimum value you require
        } catch (NumberFormatException e) {
            return false;
        }
    }
}