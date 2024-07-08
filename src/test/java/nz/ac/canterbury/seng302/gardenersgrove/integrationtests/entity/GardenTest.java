package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class GardenTest {

    private static Validator validator;
    private GardenDTO garden;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeGarden() {
        garden = new GardenDTO("Garden","1","Ilam Road","Ilam","Christchurch","New Zealand","8041",0.24,3.66,"big",null);
    }

    /**
     * Helper method to create a list of tags
     */
    private List<Tag> tags(String... names) {
        return Arrays.stream(names).map(Tag::new).toList();
    }

    @Test
    void gardenName_NameIsGarden_ReturnsEmptyConstraintViolationList() {
        garden.setName("Garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasSpace_ReturnsEmptyConstraintViolationList() {
        garden.setName("my garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasSpaces_ReturnsEmptyConstraintViolationList() {
        garden.setName("my      garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasCommas_ReturnsEmptyConstraintViolationList() {
        garden.setName("my,,garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasHyphens_ReturnsEmptyConstraintViolationList() {
        garden.setName("my--garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasApostrophes_ReturnsEmptyConstraintViolationList() {
        garden.setName("john's garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasNumbers_ReturnsEmptyConstraintViolationList() {
        garden.setName("my garden 2");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_NameHasExclamationMark_ReturnPatternConstraintViolation() {
        garden.setName("garden!");
        String expectedMessage = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenName_NameHasHash_ReturnPatternConstraintViolation() {
        garden.setName("garden #2");
        String expectedMessage = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    void gardenName_Null_ReturnNotBlankViolation() {
        garden.setName(null);
        String expectedMessage = "Garden name cannot be empty";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenLocation_NameIsValid_ReturnsEmptyConstraintViolationList() {
        garden.setCity("Christchurch");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasSpace_ReturnsEmptyConstraintViolationList() {
        garden.setCountry("New Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasSpaces_ReturnsEmptyConstraintViolationList() {
        garden.setCountry("New      Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasCommas_ReturnsEmptyConstraintViolationList() {
        garden.setCountry("Ilam, Christchurch, New Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasHyphens_ReturnsEmptyConstraintViolationList() {
        garden.setCity("Christchurch-New-Zealand");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasApostrophes_ReturnsEmptyConstraintViolationList() {
        garden.setCity("House's");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasNumbers_ReturnsEmptyConstraintViolationList() {
        garden.setCity("2street");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenLocation_LocationHasExclamationMark_ReturnPatternConstraintViolation() {
        garden.setCity("Christchurch!");
        String expectedMessage = "Please enter a valid City name";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenLocation_LocationHasHash_ReturnPatternConstraintViolation() {
        garden.setCity("garden #2");
        String expectedMessage = "Please enter a valid City name";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    void gardenLocation_Null_ReturnsViolation() {
        garden.setCity(null);
        String expectedMessage = "City is required";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenSize_CountIsOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenSize_CountIsNegativeOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("-1");
        String expectedMessage = "Garden size must be a valid positive number (only allows numbers and a single period or comma)";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenSize_DotAsDecimalPlace_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1.5");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenSize_CommaAsDecimalPlace_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1,5");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenSize_IntegerWithNonNumericChar_ReturnPatternViolation() {
        garden.setSize("1a");
        String expectedMessage = "Garden size must be a valid positive number (only allows numbers and a single period or comma)";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenSize_NonNumericChar_ReturnPatternViolation() {
        garden.setSize("a");
        String expectedMessage = "Garden size must be a valid positive number (only allows numbers and a single period or comma)";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenSize_DoubleDotAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1..5");
        String expectedMessage = "Garden size must be a valid positive number (only allows numbers and a single period or comma)";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenSize_DoubleCommaAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1,,5");
        String expectedMessage = "Garden size must be a valid positive number (only allows numbers and a single period or comma)";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_IsEmpty_ReturnNoViolations() {
        garden.setDescription("");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_IsSpaces_ReturnPatternViolation() {
        garden.setDescription("     ");
        String expectedMessage = "Description must be 512 characters or less and contain some text";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_IsFiveHundredAndThirteenChars_ReturnPatternViolation() {
        garden.setDescription("a".repeat(513));
        String expectedMessage = "Description must be 512 characters or less and contain some text";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_IsFiveHundredAndTwelveChars_ReturnsNoViolation() {
        garden.setDescription("a".repeat(512));

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_HasAlphaCharsAndSpecialChars_ReturnNoViolations() {
        garden.setDescription("large!");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_HasAlphaCharsAndNumericChars_ReturnNoViolations() {
        garden.setDescription("my 2nd garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_HasJustNumbers_ReturnPatternViolation() {
        garden.setDescription("123");
        String expectedMessage = "Description must be 512 characters or less and contain some text";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_HasJustSpecialChars_ReturnPatternViolation() {
        garden.setDescription("!!!");
        String expectedMessage = "Description must be 512 characters or less and contain some text";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_HasSpecialCharsAndNumbers_ReturnPatternViolation() {
        garden.setDescription("100!");
        String expectedMessage = "Description must be 512 characters or less and contain some text";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void gardenDescription_StartsWithNumber_ReturnsNoViolations() {
        garden.setDescription("2nd largest garden in christchurch!");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenName_ContainsDiacritic_whenSubmitted_ReturnsNoViolations() {
        garden.setName("Māori Garden");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenCity_ContainsDiacritic_whenSubmitted_ReturnsNoViolations() {
        garden.setCity("Ōtautahi");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenSuburb_ContainsDiacritic_whenSubmitted_ReturnsNoViolations() {
        garden.setSuburb("Ōtākaro");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenCountry_ContainsDiacritic_whenSubmitted_ReturnsNoViolations() {
        garden.setCountry("Aotearoa");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void enterEmptyGardenName_whenSubmitted_ReturnsViolation() {
        garden.setName(" ");
        String expectedMessage = "Garden name cannot be empty";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }

    @Test
    void enterEmptyStreetName_whenSubmitted_ReturnsNoViolation() {
        garden.setStreetName(" ");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void enterEmptyStreetNumber_whenSubmitted_ReturnsViolation() {
        garden.setStreetNumber(" ");
        String expectedMessage = "Please enter a valid street number";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    void enterEmptyCity_whenSubmitted_ReturnsViolation() {
        garden.setCity(" ");
        String expectedMessage = "City is required";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    void enterEmptyCountry_whenSubmitted_ReturnsViolation() {
        garden.setCountry(" ");
        String expectedMessage = "Country is required";
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());
    }
    @Test
    void enterEmptyPostCode_whenSubmitted_ReturnsViolation() {
        garden.setPostCode(" ");
        String expectedMessage = "Please enter a valid post code";
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(expectedMessage, violation.getMessage());


    }

    @Test
    void enterEmptySuburb_whenSubmitted_ReturnsNoViolation() {
        garden.setSuburb(" ");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void whenGetTagsStringCalled_thenReturnsCommaSeparatedString() {
        garden.getTags().addAll(tags("tag1", "tag2"));
        assertEquals("tag1,tag2", garden.getTagsString());
    }
}
