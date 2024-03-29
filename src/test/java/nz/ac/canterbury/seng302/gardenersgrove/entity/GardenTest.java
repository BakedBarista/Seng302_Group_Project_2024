package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationGroups;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GardenTest {

    private static Validator validator;
    private Garden garden;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeGarden() {
        garden = new Garden("Garden", "Christchurch", "100", "Big");
    }

    @Test
    public void gardenName_NameIsGarden_ReturnsEmptyConstraintViolationList() {
        garden.setName("Garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasSpace_ReturnsEmptyConstraintViolationList() {
        garden.setName("my garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasSpaces_ReturnsEmptyConstraintViolationList() {
        garden.setName("my      garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasCommas_ReturnsEmptyConstraintViolationList() {
        garden.setName("my,,garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasHyphens_ReturnsEmptyConstraintViolationList() {
        garden.setName("my--garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasApostrophes_ReturnsEmptyConstraintViolationList() {
        garden.setName("john's garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasNumbers_ReturnsEmptyConstraintViolationList() {
        garden.setName("my garden 2");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenName_NameHasExclamationMark_ReturnPatternConstraintViolation() {
        garden.setName("garden!");
        String expectedMessage = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenName_NameHasHash_ReturnPatternConstraintViolation() {
        garden.setName("garden #2");
        String expectedMessage = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    public void gardenName_Null_ReturnNotBlankViolation() {
        garden.setName(null);
        String expectedMessage = "Garden name cannot be empty";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenLocation_NameIsValid_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("Christchurch");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasSpace_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("New Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasSpaces_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("New      Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasCommas_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("Ilam, Christchurch, New Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasHyphens_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("Christchurch-New-Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasApostrophes_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("House's");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasNumbers_ReturnsEmptyConstraintViolationList() {
        garden.setLocation("2street");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenLocation_LocationHasExclamationMark_ReturnPatternConstraintViolation() {
        garden.setLocation("Christchurch!");
        String expectedMessage = "Location name must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenLocation_LocationHasHash_ReturnPatternConstraintViolation() {
        garden.setLocation("garden #2");
        String expectedMessage = "Location name must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    public void gardenLocation_Null_ReturnNotBlankViolation() {
        garden.setLocation(null);
        String expectedMessage = "Location cannot by empty";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenSize_CountIsOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenSize_CountIsNegativeOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("-1");
        String expectedMessage = "Garden size must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenSize_DotAsDecimalPlace_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1.5");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenSize_CommaAsDecimalPlace_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1,5");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    public void gardenSize_IntegerWithNonNumericChar_ReturnPatternViolation() {
        garden.setSize("1a");
        String expectedMessage = "Garden size must be a positive number";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenSize_NonNumericChar_ReturnPatternViolation() {
        garden.setSize("a");
        String expectedMessage = "Garden size must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenSize_DoubleDotAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1..5");
        String expectedMessage = "Garden size must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenSize_DoubleCommaAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1,,5");
        String expectedMessage = "Garden size must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Garden> violation = validator.validate(garden, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void gardenDescription_IsFiveHundredAndThirteenChars_ReturnPatternViolation() {

    }

    @Test
    public void gardenDescription_Is
}
