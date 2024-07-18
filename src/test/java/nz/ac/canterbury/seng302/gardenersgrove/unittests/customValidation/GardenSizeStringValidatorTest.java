package nz.ac.canterbury.seng302.gardenersgrove.unittests.customValidation;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Null;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.GardenSizeStringValidator;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidGardenSizeString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class GardenSizeStringValidatorTest {

    private GardenSizeStringValidator validator;

    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        context = mock(ConstraintValidatorContext.class);
        validator = new GardenSizeStringValidator();
    }

    @Test
    void givenStringIsLessThanMax_ReturnTrue() {
        String input = "1";
        int max = 2;

        ValidGardenSizeString constraintAnnotation = mock(ValidGardenSizeString.class);
        Mockito.when(constraintAnnotation.max()).thenReturn(max);
        validator.initialize(constraintAnnotation);

        Assertions.assertTrue(validator.isValid(input, context));
    }

    @Test
    void givenStringIsEqualToMax_ReturnTrue() {
        String input = "2";
        int max = 2;

        ValidGardenSizeString constraintAnnotation = mock(ValidGardenSizeString.class);
        Mockito.when(constraintAnnotation.max()).thenReturn(max);
        validator.initialize(constraintAnnotation);

        Assertions.assertTrue(validator.isValid(input, context));
    }

    @Test
    void givenStringIsGreaterThanMax_ReturnFalse() {
        String input = "2";
        int max = 1;

        ValidGardenSizeString constraintAnnotation = mock(ValidGardenSizeString.class);
        Mockito.when(constraintAnnotation.max()).thenReturn(max);
        validator.initialize(constraintAnnotation);

        Assertions.assertFalse(validator.isValid(input, context));
    }

    /**
     * Note: This is here because we only care about VALID inputs at this point
     * Strings such as "12e100" would fail in a different part of validation chain
     * @param input
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "         ", "abc123", "12e100"})
    void givenStringIsEmpty_ReturnTrue(String input) {
        int max = 1;

        ValidGardenSizeString constraintAnnotation = mock(ValidGardenSizeString.class);
        Mockito.when(constraintAnnotation.max()).thenReturn(max);
        validator.initialize(constraintAnnotation);

        Assertions.assertTrue(validator.isValid(input, context));
    }
}
