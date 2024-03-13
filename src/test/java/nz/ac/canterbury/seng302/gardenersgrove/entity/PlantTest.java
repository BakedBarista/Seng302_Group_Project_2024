package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationGroups;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlantTest {

    private static Validator validator;
    private Plant plant;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeGarden() {
        plant = new Plant("Plant", "1", "Yellow", "09/09/2023");
    }

    @Test
    public void SetName_NameIsPlant_ReturnsEmptyConstraintViolationList() {
        plant.setName("plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasSpace_ReturnsEmptyConstraintViolationList() {
        plant.setName("my plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasSpaces_ReturnsEmptyConstraintViolationList() {
        plant.setName("my      plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasCommas_ReturnsEmptyConstraintViolationList() {
        plant.setName("my,,plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasHyphens_ReturnsEmptyConstraintViolationList() {
        plant.setName("my--plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasApostrophes_ReturnsEmptyConstraintViolationList() {
        plant.setName("john's plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasNumbers_ReturnsEmptyConstraintViolationList() {
        plant.setName("my plant 2");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasExclamationMark_ReturnPatternConstraintViolation() {
        plant.setName("plant!");
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void SetName_NameHasHash_ReturnPatternConstraintViolation() {
        plant.setName("plant #2");
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    public void SetName_Null_ReturnNotBlankViolation() {
        plant.setName(null);
        String expectedMessage = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void SetCount_Null_ReturnsEmptyConstraintViolationList() {
        plant.setCount(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_Zero_ReturnsEmptyConstraintViolationList() {
        plant.setCount("0");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_One_ReturnsEmptyConstraintViolationList() {
        plant.setCount("1");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void setCount_NegativeOne_ReturnsEmptyConstraintViolationList() {
        plant.setCount("-1");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void setCount_NonIntegerChars_ReturnConstraintViolation() {
        plant.setCount("char");
        String expectedMessage = "Plant count must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void setCount_IntegerAndNonIntegerChars_ReturnConstraintViolation() {
        plant.setCount("123char");
        String expectedMessage = "Plant count must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void setCount_SpecialCharsWithInteger_ReturnConstraintViolation() {
        plant.setCount("123!");
        String expectedMessage = "Plant count must be a positive number";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.FirstOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.FirstOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void SetDescription_Null_ReturnsEmptyConstraintViolationList() {
        plant.setDescription(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_AlphanumericChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("abc123");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_AlphanumericCharsAndSpecialChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("abc123! #22");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_FiveHundredAndElevenChars_ReturnsEmptyConstraintViolationList() {
        plant.setDescription("a".repeat(511));

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_FiveHundredAndTwelveChars_ReturnSizeViolation() {
        plant.setDescription("a".repeat(512));
        String expectedMessage = "Plant description must be less than 512 characters";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<Plant> violation = validator.validate(plant, ValidationGroups.SecondOrder.class).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(plant, ValidationGroups.SecondOrder.class).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    public void SetPlantedDate_Null_ReturnsEmptyConstraintViolationList() {
        plant.setPlantedDate(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_DDMMYYYY_ReturnsEmptyConstraintViolationList() {
        plant.setPlantedDate("18/02/2023");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_YYYYDDMM_ReturnPatternViolation() {
        plant.setPlantedDate("2023-02-18");
        String expectedFormat = "18/02/2023";

        String correctFormat = PlantController.refactorPlantedDate(plant.getPlantedDate());

        Assertions.assertEquals(correctFormat, expectedFormat);
    }

}
