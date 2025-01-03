package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PlantTest {

    private static Validator validator;
    private PlantDTO plant;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeGarden() {
        plant = new PlantDTO("Plant", "1", "Yellow", "01/01/1970");
    }

    @Test
    void SetName_NameIsPlant_ReturnsEmptyConstraintViolationList() {
        plant.setName("plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasSpace_ReturnsEmptyConstraintViolationList() {
        plant.setName("my plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasSpaces_ReturnsEmptyConstraintViolationList() {
        plant.setName("my      plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasCommas_ReturnsEmptyConstraintViolationList() {
        plant.setName("my,,plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasHyphens_ReturnsEmptyConstraintViolationList() {
        plant.setName("my--plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasApostrophes_ReturnsEmptyConstraintViolationList() {
        plant.setName("john's plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasNumbers_ReturnsEmptyConstraintViolationList() {
        plant.setName("my plant 2");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetName_NameHasExclamationMark_ReturnPatternConstraintViolation() {
        plant.setName("plant!");
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<PlantDTO> violation = validator.validate(plant).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void SetName_NameHasHash_ReturnPatternConstraintViolation() {
        plant.setName("plant #2");
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<PlantDTO> violation = validator.validate(plant).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void WhenSetNameToEmptyStringReturnNotBlankViolation() {
        plant.setName(" ");
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;
        assertEquals(validator.validate(plant).size(), expectedConstraintSetSize);
        assertEquals(expectedMessage, validator.validate(plant).iterator().next().getMessage());
        }
    @Test
    void SetName_Null_ReturnNotBlankViolation() {
        plant.setName(null);
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<PlantDTO> violation = validator.validate(plant).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "0", "1", "42.0" })
    void whenSetCountToValidValue_thenNoError(String value) {
        plant.setCount(value);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "char", "123char", "123!"})
    void whenSetCountToInvalidValue_thenErrors(String value) {
        plant.setCount(value);

        assertEquals(1, validator.validate(plant).size());
        ConstraintViolation<PlantDTO> violation = validator.validate(plant).iterator().next();
        assertEquals("Plant count must be a positive whole number", violation.getMessage());
    }

    @Test
    void SetDescription_Null_ReturnsEmptyConstraintViolationList() {
        plant.setDescription(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetDescription_AlphanumericChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("abc123");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetDescription_AlphanumericCharsAndSpecialChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("abc123! #22");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetDescription_FiveHundredAndElevenChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("a".repeat(511));

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void WhenSetDescriptionLengthTo513CharactersWhenSubmitReturnErrorMessage() {
        plant.setDescription("a".repeat(513));
        String expectedMessage = "Plant description must be less than 512 characters";
        Integer expectedConstraintSetSize = 1;
        ConstraintViolation<PlantDTO> violation = validator.validate(plant).iterator().next();
        assertEquals(expectedConstraintSetSize, validator.validate(plant).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void SetPlantedDate_Null_ReturnsEmptyConstraintViolationList() {
        plant.setPlantedDate(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetPlantedDate_DDMMYYYY_ReturnsEmptyConstraintViolationList() {
        plant.setPlantedDate("01/01/2000");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    void SetPlantedDate_YYYYDDMM_ReturnPatternViolation() {
        plant.setPlantedDate("2000/01/01");
        Integer expectedConstraintSetSize = 1;

        assertEquals(expectedConstraintSetSize, validator.validate(plant).size());
    }

    @Test
    void givenPlantNameTooLong_whenFormSubmitted_thenReturnsConstraintViolationList() {
        plant.setName("a".repeat(257));

        assertFalse(validator.validate(plant).isEmpty());
    }

    @Test
    void givenPlantNameHasValidLength_whenFormSubmitted_thenReturnsEmptyConstraintViolationList() {
        plant.setName("a".repeat(256));

        assertTrue(validator.validate(plant).isEmpty());
    }
}
