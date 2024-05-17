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
    }

    @ParameterizedTest
    @ValueSource(strings = {"2003-01-31", "2003-02-29", "2003-03-31", "2003-04-30", "2003-05-31", "2003-06-30",
            "2003-07-31", "2003-08-31", "2003-09-30", "2003-10-31", "2003-11-30", "2003-12-31"})
    public void givenDateIsInTheThymeleafFormat_AndIsValid_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"31/01/2003", "29/02/2003", "31/03/2003", "30/04/2003", "31/05/2003", "30/06/2003",
            "31/07/2003", "31/08/2003", "30/09/2003", "31/10/2003", "30/11/2003", "31/12/2003"})
    public void givenDateIsInThePersistedFormat_AndIsValid_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertTrue(isValid);
    }

    @Test
    public void givenDateIsInTheThymeleafFormat_AndMonthIsTooLarge_ReturnTrue() {
        String invalidDateValidFormat = "2003-31-31";

        boolean isValid = dateValidator.isValid(invalidDateValidFormat, context);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void givenDateIsInThePersistedFormat_AndMonthIsTooLarge_ReturnTrue() {
        String invalidDateValidFormat = "31/31/2003";

        boolean isValid = dateValidator.isValid(invalidDateValidFormat, context);

        Assertions.assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2003-01-32", "2003-02-30", "2003-03-32", "2003-04-31", "2003-05-32", "2003-06-31",
            "2003-07-32", "2003-08-32", "2003-09-31", "2003-10-32", "2003-11-31", "2003-12-32"})
    public void givenDateIsInTheThymeleafFormat_AndDayIsTooLarge_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"32/01/2003", "30/02/2003", "32/03/2003", "31/04/2003", "32/05/2003", "31/06/2003",
            "32/07/2003", "32/08/2003", "31/09/2003", "32/10/2003", "31/11/2003", "32/12/2003"})
    public void givenDateIsInThePersistedFormat_AndDayIsTooLarge_ReturnTrue(String date) {
        boolean isValid = dateValidator.isValid(date, context);

        Assertions.assertFalse(isValid);
    }

}
