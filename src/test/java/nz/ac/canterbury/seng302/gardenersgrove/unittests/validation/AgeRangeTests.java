package nz.ac.canterbury.seng302.gardenersgrove.unittests.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.gardenersgrove.validation.AgeRange;

public class AgeRangeTests {
    private ConstraintValidatorContext context;
    private AgeRange.Validator validator;

    @BeforeEach
    public void setUp() {
        Instant instant = Instant.parse("2024-01-01T00:00:00Z");
        ZoneId zone = ZoneId.of("UTC");
        ClockProvider clockProvider = mock(ClockProvider.class);
        when(clockProvider.getClock()).thenReturn(Clock.fixed(instant, zone));

        context = mock(ConstraintValidatorContext.class);
        when(context.getClockProvider()).thenReturn(clockProvider);

        validator = new AgeRange.Validator();
    }

    @Test
    public void testAgeAtStartOfRange_ReturnsTrue() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("2006-01-01", context);

        assertTrue(result);
    }

    @Test
    public void testAgeAtEndOfRange_ReturnsTrue() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("1924-01-01", context);

        assertTrue(result);
    }

    @Test
    public void testAgeInRange_ReturnsTrue() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("2000-01-01", context);

        assertTrue(result);
    }

    @Test
    public void testAgeBelowRange_ReturnsFalse() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("2007-01-01", context);

        assertFalse(result);
    }

    @Test
    public void testAgeAboveRange_ReturnsFalse() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("1900-01-01", context);

        assertFalse(result);
    }

    @Test
    public void testDateIsNull_ReturnsTrue() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid(null, context);

        assertTrue(result);
    }

    @Test
    public void testInvalidDate_ReturnsTrue() {
        AgeRange annotation = mock(AgeRange.class);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(100);

        validator.initialize(annotation);
        boolean result = validator.isValid("0-01-01", context);

        assertTrue(result);
    }
}
