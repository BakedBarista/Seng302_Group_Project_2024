package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.entity;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

class GardenDTOTest {

    private static Validator validator;
    private GardenDTO garden;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void makeGarden() {
        garden = new GardenDTO("Garden","1","Ilam Road","Ilam","Christchurch","New Zealand","8041",0.24,3.66,"big",null,"Token123", null, null);
    }

    /**
     * Helper method to create a list of tags
     */
    private List<Tag> tags(String... names) {
        return Arrays.stream(names).map(Tag::new).toList();
    }

    @Test
    void gardenName_NameIsTooLong_ReturnsConstraintViolation() {
        garden.setName("a".repeat(101));

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(1, validator.validate(garden).size());
        assertEquals(MAX_GARDEN_NAME_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenName_NameIsOKLength_ReturnsEmptyConstraintViolationList() {
        garden.setName("a".repeat(100));

        assertTrue(validator.validate(garden).isEmpty());
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
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_NAME_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenName_NameHasHash_ReturnPatternConstraintViolation() {
        garden.setName("garden #2");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_NAME_MESSAGE, violation.getMessage());
    }
    @Test
    void gardenName_Null_ReturnNotBlankViolation() {
        garden.setName(null);
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(EMPTY_GARDEN_NAME_MESSAGE, violation.getMessage());
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
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_CITY_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenLocation_LocationHasHash_ReturnPatternConstraintViolation() {
        garden.setCity("garden #2");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_CITY_MESSAGE, violation.getMessage());
    }
    @Test
    void gardenLocation_Null_ReturnsViolation() {
        garden.setCity(null);
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_CITY_REQUIRED_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_CountIsOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenSize_CountIsNegativeOne_ReturnsEmptyConstraintViolationList() {
        garden.setSize("-1");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
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
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_NumberIsExponentialWithE_ReturnPatternViolation() {
        garden.setSize("1e10");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_NonNumericChar_ReturnPatternViolation() {
        garden.setSize("a");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_DoubleDotAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1..5");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_DoubleCommaAsDecimalPlace_ReturnPatternViolation() {
        garden.setSize("1,,5");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_SIZE_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_SizeIsTooLong_ReturnsConstraintViolation() {
        garden.setSize("1." + "0".repeat(49));

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(1, validator.validate(garden).size());
        assertEquals(MAX_GARDEN_SIZE_LENGTH_MESSAGE, violation.getMessage());
    }

    @Test
    void gardenSize_SizeIsOKLength_ReturnsEmptyConstraintViolationList() {
        garden.setSize("1." + "0".repeat(48));

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_IsEmpty_ReturnNoViolations() {
        garden.setDescription("");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void gardenDescription_IsSpaces_ReturnPatternViolation() {
        garden.setDescription("     ");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_DESCRIPTION, violation.getMessage());
    }

    @Test
    void gardenDescription_IsFiveHundredAndThirteenChars_ReturnPatternViolation() {
        garden.setDescription("a".repeat(513));
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_DESCRIPTION, violation.getMessage());
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
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_DESCRIPTION, violation.getMessage());
    }

    @Test
    void gardenDescription_HasJustSpecialChars_ReturnPatternViolation() {
        garden.setDescription("!!!");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_DESCRIPTION, violation.getMessage());
    }

    @Test
    void gardenDescription_HasSpecialCharsAndNumbers_ReturnPatternViolation() {
        garden.setDescription("100!");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(INVALID_GARDEN_DESCRIPTION, violation.getMessage());
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
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(EMPTY_GARDEN_NAME_MESSAGE, violation.getMessage());
    }

    @Test
    void enterEmptyStreetName_whenSubmitted_ReturnsNoViolation() {
        garden.setStreetName(" ");

        assertTrue(validator.validate(garden).isEmpty());
    }

    @Test
    void enterEmptyStreetNumber_whenSubmitted_ReturnsViolation() {
        garden.setStreetNumber(" ");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_STREET_NUMBER_MESSAGE, violation.getMessage());
    }

    @Test
    void enterEmptyCity_whenSubmitted_ReturnsViolation() {
        garden.setCity(" ");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_CITY_REQUIRED_MESSAGE, violation.getMessage());
    }

    @Test
    void enterEmptyCountry_whenSubmitted_ReturnsViolation() {
        garden.setCountry(" ");
        Integer expectedConstraintSetSize = 1;


        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_COUNTRY_REQUIRED_MESSAGE, violation.getMessage());
    }

    @Test
    void enterEmptyPostCode_whenSubmitted_ReturnsViolation() {
        garden.setPostCode(" ");
        Integer expectedConstraintSetSize = 1;

        ConstraintViolation<GardenDTO> violation = validator.validate(garden).iterator().next();

        assertEquals(expectedConstraintSetSize, validator.validate(garden).size());
        assertEquals(GARDEN_POST_CODE_MESSAGE, violation.getMessage());


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

    @Test
    void givenUniqueToken_whenSettingToken_thenTokenSaved() {
        garden.setSubmissionToken("NewToken");
        assertTrue(validator.validate(garden).isEmpty());
    }
}
