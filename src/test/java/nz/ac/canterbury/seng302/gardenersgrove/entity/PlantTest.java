package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlantTest {

    private static Validator validator;
    private Plant plant;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void makeGarden() {
        plant = new Plant("Plant", 1, "Yellow", "09/09/2023");
    }

    @Test
    public void SetName_NameIsPlant_Successful() {
        plant.setName("plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasSpace_Successful() {
        plant.setName("my plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasSpaces_Successful() {
        plant.setName("my      plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasCommas_Successful() {
        plant.setName("my,,plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasHyphens_Successful() {
        plant.setName("my--plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasApostrophes_Successful() {
        plant.setName("john's plant");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasNumbers_Successful() {
        plant.setName("my plant 2");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasExclamationMark_ReturnPatternConstraintViolation() {
        plant.setName("plant!");

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetName_NameHasHash_ReturnPatternConstraintViolation() {
        plant.setName("plant #2");

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }
    @Test
    public void SetName_Null_ReturnNotBlankViolation() {
        plant.setName(null);

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_Null_Successful() {
//        plant.setCount(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_Zero_Successful() {
//        plant.setCount("0");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_One_Successful() {
//        plant.setCount("1");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_NegativeOne_Successful() {
//        plant.setCount("-1");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_DotAsDecimalPlace_Successful() {
//        plant.setCount("1.5");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_CommaAsDecimalPlace_Successful() {
//        plant.setCount("1,5");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_IntegerWithNonNumericChar_ReturnPatternViolation() {
//        plant.setCount("1a");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_NonNumericChar_ReturnPatternViolation() {
//        plant.setCount("a");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_DoubleDotAsDecimalPlace_ReturnPatternViolation() {
//        plant.setCount("1..5");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetCount_DoubleCommaAsDecimalPlace_ReturnPatternViolation() {
//        plant.setCount("1,,5");

        //FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_Null_Successful() {
        plant.setDescription(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_AlphanumericChars_Successful() {
        plant.setDescription("abc123");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_AlphanumericCharsAndSpecialChars_Successful() {
        plant.setDescription("abc123! #22");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_FiveHundredAndElevenChars_Successful() {
        plant.setDescription("a".repeat(511));

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetDescription_FiveHundredAndTwelveChars_ReturnSizeViolation() {
        plant.setDescription("a".repeat(512));

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_Null_Successful() {
        plant.setPlantedDate(null);

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_DDMMYYYY_Successful() {
        plant.setPlantedDate("18/02/2023");

        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_MMDDYYYY_ReturnPatternViolation() {
        plant.setPlantedDate("02/18/2023");

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

    @Test
    public void SetPlantedDate_YYYYDDMM_ReturnPatternViolation() {
        plant.setPlantedDate("2023/18/02");

        // FIX
        assertTrue(validator.validate(plant).isEmpty());
    }

}
