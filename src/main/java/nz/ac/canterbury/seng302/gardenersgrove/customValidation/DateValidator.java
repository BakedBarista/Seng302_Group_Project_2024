package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private final List<Integer> THIRTY_DAY_MONTHS = List.of(4, 6, 9, 11);
    private final List<Integer> THIRTY_ONE_DAY_MONTHS = List.of(1, 3, 5, 7, 8, 10, 12);

    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    @Override
    public void initialize(ValidDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String dateField, ConstraintValidatorContext context) {
        if (dateField == null || dateField.isEmpty()) {
            return true;
        }

        if (!Pattern.matches(DATE_PATTERN, dateField)) {
            return false;
        }

        String[] fields = dateField.split("-");
        int day = Integer.parseInt(fields[1], 10);
        int month = Integer.parseInt(fields[2], 10);
        int year = Integer.parseInt(fields[0], 10);


        boolean dayValid = day > 0 && day <= 31;
        boolean monthValid = month > 0 && month <= 12;
        boolean yearValid = year > 0;

        if (day == 31 && !THIRTY_ONE_DAY_MONTHS.contains(month)) {
            return false;
        } else if (day == 30 && !(THIRTY_ONE_DAY_MONTHS.contains(month) || THIRTY_DAY_MONTHS.contains(month))) {
            return false;
        }

        return dayValid && monthValid && yearValid;
    }
}

