package nz.ac.canterbury.seng302.gardenersgrove.unittests.customValidation;

import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.DateValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class DateValidationTest {

    public static DateValidator dateValidator;

    public static ConstraintValidatorContext context;

    @BeforeAll
    public static void setup() {
        dateValidator = new DateValidator();
        context = mock(ConstraintValidatorContext.class);

        // note : dates given from thymeleaf in dd-MM-yyyy format and then converted in java after validation
    }

    @ParameterizedTest
    @ValueSource(strings = {"2003-31-01", "2003-29-02", "2003-31-03", "2003-30-04", "2003-31-05", "2003-30-06",
            "2003-31-07", "2003-31-08", "2003-30-09", "2003-31-10", "2003-30-11", "2003-31-12"})
    public void givenDateIsInTheExpectedFormat_AndIsValid_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertTrue(isValid);
    }

    @Test
    public void givenDateIsInTheExpectedFormat_AndMonthIsTooLarge_ReturnTrue() {
        String invalidDateValidFormat = "2003-31-31";

        boolean isValid = dateValidator.isValid(invalidDateValidFormat, context);

        Assertions.assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2003-32-01", "2003-30-02", "2003-32-03", "2003-31-04", "2003-32-05", "2003-31-06",
            "2003-32-07", "2003-32-08", "2003-31-09", "2003-32-10", "2003-31-11", "2003-32-12"})
    public void givenDateIsInTheExpectedFormat_AndDayIsTooLarge_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"09/08/2003", "09-08-2003", "09-2003-08"})
    public void givenDateIsInTheWrongFormat_AndIsValid_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertFalse(isValid);
    }
}
