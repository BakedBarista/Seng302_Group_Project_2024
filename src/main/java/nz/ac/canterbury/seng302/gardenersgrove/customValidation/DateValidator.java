package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Deals with validating dates
 */
public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private static final List<Integer> THIRTY_DAY_MONTHS = List.of(4, 6, 9, 11);
    private static final List<Integer> THIRTY_ONE_DAY_MONTHS = List.of(1, 3, 5, 7, 8, 10, 12);

    private static final String DATE_PATTERN_THYMELEAF = "^\\d{4}-\\d{2}-\\d{2}$";

    private static final String DATE_PATTERN_STORED = "^\\d{2}/\\d{2}/\\d{4}$";

    /**
     * validate a given date string as long as it is not empty.
     * This deals with making sure each value of the date is within its correct bound
     * and deals with issues like 30/30/2002 or 29th of feb on a non-leap year
     *
     * @param date object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if date is valid
     */
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null || date.isEmpty()) {
            return true;
        }

        int day;
        int month;
        int year;
        if (Pattern.matches(DATE_PATTERN_THYMELEAF, date)) {
            String[] fields = date.split("-");
            day = Integer.parseInt(fields[2], 10);
            month = Integer.parseInt(fields[1], 10);
            year = Integer.parseInt(fields[0], 10);
        } else if (Pattern.matches(DATE_PATTERN_STORED, date)) {
            String[] fields = date.split("/");
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
        } else if (day == 29 && month == 2 && !isLeapYear(year)) {
            // 29th of feb on a leap year case
            return false;
        }

        return dayValid && monthValid && yearValid;
    }

    /**
     * check if given year is a leap year
     * note: asked chatGPT for this
     * @param year to be checked for leap year
     * @return true if leap year
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}

