package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private static final List<Integer> THIRTY_DAY_MONTHS = List.of(4, 6, 9, 11);
    private static final List<Integer> THIRTY_ONE_DAY_MONTHS = List.of(1, 3, 5, 7, 8, 10, 12);

    private static final String DATE_PATTERN_THYMELEAF = "^\\d{4}-\\d{2}-\\d{2}$";

    private static final String DATE_PATTERN_STORED = "^\\d{2}/\\d{2}/\\d{4}$";

    @Override
    public boolean isValid(String dateField, ConstraintValidatorContext context) {
        if (dateField == null || dateField.isEmpty()) {
            return true;
        }

        int day;
        int month;
        int year;
        if (Pattern.matches(DATE_PATTERN_THYMELEAF, dateField)) {
            String[] fields = dateField.split("-");
            day = Integer.parseInt(fields[2], 10);
            month = Integer.parseInt(fields[1], 10);
            year = Integer.parseInt(fields[0], 10);
        } else if (Pattern.matches(DATE_PATTERN_STORED, dateField)) {
            String[] fields = dateField.split("/");
            day = Integer.parseInt(fields[0], 10);
            month = Integer.parseInt(fields[1], 10);
            year = Integer.parseInt(fields[2], 10);
        } else {
            return false;
        }

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

